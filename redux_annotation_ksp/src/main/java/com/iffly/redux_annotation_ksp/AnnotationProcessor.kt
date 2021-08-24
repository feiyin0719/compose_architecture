package com.iffly.redux_annotation_ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.iffly.redux.annotation.Reducer

class AnnotationProcessor(val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    companion object {
        public val REDUCER_NAME: String = requireNotNull(Reducer::class.qualifiedName)
    }

    val logger = environment.logger

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
                logger.info("myyf", reducer)

            }

        return emptyList()
    }
}