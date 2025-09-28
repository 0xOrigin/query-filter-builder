package io.github._0xorigin.queryfilterbuilder.base.filterfield;

import java.io.Serializable;

/**
 * An abstract base class for implementing {@link FilterField} and {@link FieldCaster}.
 * It extends {@link OperatorSupport} to inherit operator management functionality.
 *
 * @param <T> The Java type that this filter field represents.
 */
public abstract class AbstractFilterField<T extends Comparable<? super T> & Serializable>
    extends OperatorSupport
    implements FilterField<T>, FieldCaster<T>
{ }
