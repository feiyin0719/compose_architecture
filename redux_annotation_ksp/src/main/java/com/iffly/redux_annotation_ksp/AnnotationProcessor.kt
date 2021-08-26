package com.iffly.redux_annotation_ksp

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.iffly.redux.annotation.Reducer
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.concurrent.thread

class AnnotationProcessor(val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    companion object {
        val REDUCER_NAME: String = requireNotNull(Reducer::class.qualifiedName)
    }

    private val codeGenerator = environment.codeGenerator
    val logger = environment.logger

    val list = mutableListOf<KSClassDeclaration>()

    inner class ReducerVisitor(val list: MutableList<KSClassDeclaration>) : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            list.add(classDeclaration)
        }
    }

    @ExperimentalStdlibApi
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("myyf start")
        val reducerType = resolver.getClassDeclarationByName(
            resolver.getKSNameFromString(REDUCER_NAME)
        )?.asType(emptyList()) ?: return emptyList()
        val visitor = ReducerVisitor(list)
        resolver.getSymbolsWithAnnotation(REDUCER_NAME)
            .asSequence()
            .filterIsInstance<KSClassDeclaration>()
            .forEach { reducer ->
                val annotation =
                    reducer.annotations.find { it.annotationType.resolve() == reducerType }
                annotation?.let { reducer.accept(visitor, Unit) }


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


                val type = ArrayList::class.asClassName().parameterizedBy(reducerType)
                logger.warn("myyf ${reducerType}-${list.size}")
                val packageName = "com.iffly.compose.libredux";
                val reduxContainerSpec =
                    TypeSpec.objectBuilder("ReduxListContainer")
                        .addKdoc(
                            CodeBlock.of("auto-generated by Redux, do not modify")
                        ).apply {
                            addProperty(
                                PropertySpec.builder(
                                    "reducers",
                                    type, KModifier.PUBLIC
                                ).initializer(
                                    "ArrayList<%T>()", reducerType
                                ).build()
                            )
                            val initBlockBuilder = CodeBlock.builder()
                            list.forEach {
                                initBlockBuilder.addStatement(
                                    """
                                    reducers.add(%T())
                                """.trimIndent(),
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
                val storeInitFunSpec = FunSpec.builder("storeViewModelInit").returns(
                    storeViewModelType
                ).addAnnotation(composeAnnotationType)
                    .addModifiers(KModifier.PUBLIC)
                    .addStatement(
                        "val reducers = ReduxListContainer.reducers"
                    ).addStatement(
                        "return storeViewModel(list = reducers)"
                    )
                    .build()

                val dependencies = Dependencies(true)
                val fileSpec = FileSpec.builder(packageName, "ReduxListContainer")
                    .addType(reduxContainerSpec)
                    .addFunction(storeInitFunSpec)
                    .build()


                codeGenerator.createNewFile(
                    dependencies = dependencies,
                    packageName = packageName,
                    fileName = "ReduxListContainer",
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