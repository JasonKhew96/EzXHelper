@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.kyuubiran.ezxhelper.finders

import com.github.kyuubiran.ezxhelper.interfaces.IFindSuper
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Helper for finding field(s) in the class or collection.
 */
class FieldFinder private constructor(seq: Sequence<Field>) : BaseMemberFinder<Field, FieldFinder>(seq), IFindSuper<FieldFinder> {
    private var clazz: Class<*>? = null

    @Suppress("ClassName")
    companion object `-Static` {
        @JvmStatic
        fun fromClass(clazz: Class<*>): FieldFinder {
            return FieldFinder(clazz.declaredFields.asSequence()).also { it.clazz = clazz }
        }

        @JvmStatic
        fun fromSequence(seq: Sequence<Field>): FieldFinder {
            return FieldFinder(seq)
        }

        @JvmStatic
        fun fromArray(array: Array<Field>): FieldFinder {
            return FieldFinder(array.asSequence())
        }

        @JvmStatic
        fun fromVararg(vararg array: Field): FieldFinder {
            return FieldFinder(array.asSequence())
        }

        @JvmStatic
        fun fromIterable(iterable: Iterable<Field>): FieldFinder {
            return FieldFinder(iterable.asSequence())
        }

        @JvmSynthetic
        fun Class<*>.fieldFinder() = fromClass(this)

        @JvmSynthetic
        fun Array<Field>.fieldFinder() = fromArray(this)

        @JvmSynthetic
        fun Iterable<Field>.fieldFinder() = fromIterable(this)

        @JvmSynthetic
        fun Sequence<Field>.fieldFinder() = fromSequence(this)
    }

    // region filter by
    /**
     * Filter by field name.
     * @param name The name of the field.
     * @return [FieldFinder] this finder.
     */
    fun filterByName(name: String) = applyThis { filter { this.name == name } }

    /**
     * Filter by field type.
     * @param type The type of the field.
     * @return [FieldFinder] this finder.
     */
    fun filterByType(type: Class<*>) = applyThis { filter { this.type == type } }
    // endregion

    // region filter modifiers
    /**
     * Filter if they are static.
     * @return [FieldFinder] this finder.
     */
    fun filterStatic() = filterIncludeModifiers(Modifier.STATIC)

    /**
     * Filter if they are non-static.
     * @return [FieldFinder] this finder.
     */
    fun filterNonStatic() = filterExcludeModifiers(Modifier.STATIC)
    // endregion

    // region overrides
    override fun findSuper(untilPredicate: (Class<*>.() -> Boolean)?) = applyThis {
        if (clazz == null || clazz == Any::class.java) return@applyThis

        var c = clazz?.superclass ?: return@applyThis

        while (c != Any::class.java) {
            memberSequence += c.declaredFields.asSequence()
            c = c.superclass
        }
    }
    // endregion
}