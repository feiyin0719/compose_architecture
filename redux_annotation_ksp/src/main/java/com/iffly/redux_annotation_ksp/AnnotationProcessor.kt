package com.iffly.redux_annotation_ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

class AnnotationProcessor :SymbolProcessor{
    override fun process(resolver: Resolver): List<KSAnnotated> {
        TODO("Not yet implemented")
    }
}