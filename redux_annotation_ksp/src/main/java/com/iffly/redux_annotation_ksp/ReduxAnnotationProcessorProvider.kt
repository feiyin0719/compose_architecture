package com.iffly.redux_annotation_ksp

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
@AutoService(SymbolProcessorProvider::class)
class ReduxAnnotationProcessorProvider:SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ReduxAnnotationProcessor(environment)
    }


}