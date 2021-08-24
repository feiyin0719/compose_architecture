package com.iffly.redux_annotation_ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.iffly.redux.annotation.Reducer
import kotlin.concurrent.thread

class AnnotationProcessor(val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    companion object {
         val REDUCER_NAME: String = requireNotNull(Reducer::class.qualifiedName)
    }

    val logger = environment.logger

    val list = mutableListOf<KSClassDeclaration>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("myyf start" )
        val reducerType = resolver.getClassDeclarationByName(
            resolver.getKSNameFromString(REDUCER_NAME)
        )?.asType(emptyList()) ?: return emptyList()

        resolver.getSymbolsWithAnnotation(REDUCER_NAME)
            .asSequence()
            .filterIsInstance<KSClassDeclaration>()
            .forEach { reducer ->
                val annotation =
                    reducer.annotations.find { it.annotationType.resolve() == reducerType }
                        ?: run {
                            return@forEach
                        }
                list.add(reducer)
            }
        logger.warn("myyf $list")
        thread(true) {
            synchronized(list){

            }
        }

        return emptyList()
    }
}