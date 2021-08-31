package com.iffly.redux_annotation_ksp

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.iffly.redux.annotation.MiddleWare
import com.iffly.redux.annotation.Reducer
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.concurrent.thread

class ReduxAnnotationProcessor(val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    companion object {
        val REDUCER_NAME: String = requireNotNull(Reducer::class.qualifiedName)
        val MIDDLEWARE_NAME: String = requireNotNull(MiddleWare::class.qualifiedName)
    }

    private val codeGenerator = environment.codeGenerator
    val logger = environment.logger

    val reducerClassDels = mutableListOf<KSClassDeclaration>()
    val middleWareClassDels = mutableListOf<KSClassDeclaration>()

    inner class ClassVisitor(val list: MutableList<KSClassDeclaration>) : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            list.add(classDeclaration)
        }
    }

    @ExperimentalStdlibApi
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("redux annotation process start")
        val reducerAnnotationType = resolver.getClassDeclarationByName(
            resolver.getKSNameFromString(REDUCER_NAME)
        )?.asType(emptyList()) ?: return emptyList()
        val middleWareAnnotationType = resolver.getClassDeclarationByName(
            resolver.getKSNameFromString(MIDDLEWARE_NAME)
        )?.asType(emptyList()) ?: return emptyList()
        val reducerVisitor = ClassVisitor(reducerClassDels)
        val middleWareVisitor = ClassVisitor(middleWareClassDels)
        resolver.getSymbolsWithAnnotation(REDUCER_NAME)
            .asSequence()
            .filterIsInstance<KSClassDeclaration>()
            .forEach { ksClassDeclaration ->

                ksClassDeclaration.annotations.find { it.annotationType.resolve() == reducerAnnotationType }
                    ?.let { ksClassDeclaration.accept(reducerVisitor, Unit) }
            }
        resolver.getSymbolsWithAnnotation(MIDDLEWARE_NAME)
            .asSequence()
            .filterIsInstance<KSClassDeclaration>()
            .forEach { ksClassDeclaration ->
                ksClassDeclaration.annotations.find { it.annotationType.resolve() == middleWareAnnotationType }
                    ?.let { ksClassDeclaration.accept(middleWareVisitor, Unit) }
            }
        thread(true) {
            synchronized(codeGenerator) {
                val reducerType =
                    ClassName.bestGuess("com.iffly.compose.libredux.Reducer").parameterizedBy(
                        listOf(
                            WildcardTypeName.producerOf(Any::class),
                            WildcardTypeName.producerOf(Any::class),
                        )
                    )

                val middleWareType = ClassName.bestGuess("com.iffly.compose.libredux.MiddleWare")
                val reducerListType = MUTABLE_LIST.parameterizedBy(reducerType)
                val middleWareListType = MUTABLE_LIST.parameterizedBy(middleWareType)
                val middleWareClassDelSort = middleWareClassDels.map { classDefine ->
                    val pri: Int = (classDefine.annotations
                        .find { it.annotationType.resolve() == middleWareAnnotationType }
                        ?.arguments?.get(0)?.value ?: -1) as Int
                    Pair(
                        pri, classDefine
                    )
                }.sortedBy {
                    it.first
                }.map {
                    it.second
                }
                val packageName = "com.iffly.compose.libredux.container"
                val containerClassName = "ReduxListContainer"
                val reducersVarName = "reducers"
                val middleWareVarName = "middleWares"
                val containerClass = ClassName(packageName, containerClassName)
                val reducerMemberName = containerClass.member(reducersVarName)
                val middleWareMemberName = containerClass.member(middleWareVarName)
                val mutableListMemberName = MemberName("kotlin.collections", "mutableListOf")
                val reduxContainerSpec =
                    TypeSpec.objectBuilder(containerClassName)
                        .addKdoc(
                            CodeBlock.of("auto-generated by Redux, do not modify")
                        ).apply {
                            addProperty(
                                PropertySpec.builder(
                                    reducersVarName,
                                    reducerListType,
                                    KModifier.PUBLIC
                                ).initializer(
                                    "%M()", mutableListMemberName
                                ).build()
                            )

                            addProperty(
                                PropertySpec.builder(
                                    middleWareVarName,
                                    middleWareListType,
                                    KModifier.PUBLIC
                                ).initializer(
                                    "%M()", mutableListMemberName
                                ).build()
                            )


                            val initBlockBuilder = CodeBlock.builder()
                            reducerClassDels.forEach {
                                initBlockBuilder.addStatement(
                                    """
                                    %M.add(%T())
                                """.trimIndent(),
                                    reducerMemberName,
                                    ClassName.bestGuess(it.qualifiedName?.asString() ?: "")
                                )
                            }

                            middleWareClassDelSort.forEach {
                                initBlockBuilder.addStatement(
                                    """
                                    %M.add(%T())
                                """.trimIndent(),
                                    middleWareMemberName,
                                    ClassName.bestGuess(it.qualifiedName?.asString() ?: "")
                                )
                            }

                            addInitializerBlock(initBlockBuilder.build())
                        }
                        .build()

                val storeViewModelType =
                    ClassName.bestGuess("com.iffly.compose.libredux.StoreViewModel")
                val composeAnnotationType =
                    ClassName.bestGuess("androidx.compose.runtime.Composable")
                val storeViewModelMemberName = MemberName(
                    "com.iffly.compose.libredux",
                    "storeViewModel"
                )

                val storeInitFunSpec = FunSpec.builder("storeViewModelInit").returns(
                    storeViewModelType
                ).addAnnotation(composeAnnotationType)
                    .addModifiers(KModifier.PUBLIC)
                    .addStatement(
                        "return %M( %M, %M )",
                        storeViewModelMemberName,
                        reducerMemberName,
                        middleWareMemberName
                    )
                    .build()

                val dependencies = Dependencies(true)
                val fileSpec = FileSpec.builder(packageName, containerClassName)
                    .addType(reduxContainerSpec)
                    .addFunction(storeInitFunSpec)
                    .build()


                codeGenerator.createNewFile(
                    dependencies = dependencies,
                    packageName = packageName,
                    fileName = containerClassName,
                    extensionName = "kt"
                ).bufferedWriter().use { writer ->
                    try {
                        fileSpec.writeTo(writer)
                    } catch (e: Exception) {

                    } finally {
                        writer.flush()
                        writer.close()
                    }
                }

            }
        }

        return emptyList()
    }
}