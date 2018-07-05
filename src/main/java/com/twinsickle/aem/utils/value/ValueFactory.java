package com.twinsickle.aem.utils.value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public final class ValueFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ValueFactory.class);

    public static Value createValue(Object item) throws RepositoryException{
        return new ValueImpl(item);
    }

    public static Value[] createValueArray(Collection<?> items){
        return items.stream()
                .map(ValueFactory::convertToValue)
                .toArray(Value[]::new);
    }

    private static Value convertToValue(Object item){
        try {
            return createValue(item);
        } catch (RepositoryException re){
            LOG.warn("ValueFactory#convertToValue - Unable to convert to value", re);
            return null;
        }
    }

    private static class ValueImpl implements Value {
        private Object value;
        private int type;

        private ValueImpl(final Object value) throws RepositoryException {
            if (value == null) {
                throw new RepositoryException("Value is null");
            }
            Object temp = value;
            if (value instanceof Boolean) {
                type = PropertyType.BOOLEAN;
            } else if (value instanceof Calendar) {
                type = PropertyType.DATE;
            } else if (value instanceof Date){
                type = PropertyType.DATE;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime((Date) value);
                temp = calendar;
            } else if (value instanceof Double) {
                type = PropertyType.DOUBLE;
            } else if (value instanceof Long) {
                type = PropertyType.LONG;
            } else if (value instanceof Integer){
                type = PropertyType.LONG;
                temp = new Long((Integer)value);
            } else if (value instanceof String) {
                type = PropertyType.STRING;
            } else if (value instanceof Binary) {
                type = PropertyType.BINARY;
            } else if (value instanceof BigDecimal){
                type = PropertyType.DECIMAL;
            } else {
                throw new RepositoryException("Unsupported type " + value.getClass().getSimpleName());
            }

            this.value = temp;
        }

        @Override
        public String getString() throws RepositoryException {
            if(type == PropertyType.STRING){
                return (String) value;
            }
            throw new ValueFormatException("Value is not a String: " + value.getClass().getSimpleName());
        }

        @Deprecated
        @Override
        public InputStream getStream() throws RepositoryException {
            throw new UnsupportedRepositoryOperationException("Unsupported operation");
        }

        @Override
        public Binary getBinary() throws RepositoryException {
            if(type == PropertyType.BINARY){
                return (Binary) value;
            }
            throw new ValueFormatException("Value is not a Binary: " + value.getClass().getSimpleName());
        }

        @Override
        public long getLong() throws RepositoryException {
            if(type == PropertyType.LONG){
                return (Long) value;
            }
            throw new ValueFormatException("Value is not a Long: " + value.getClass().getSimpleName());
        }

        @Override
        public double getDouble() throws RepositoryException {
            if(type == PropertyType.DOUBLE){
                return (Double) value;
            }
            throw new ValueFormatException("Value is not a Double: " + value.getClass().getSimpleName());
        }

        @Override
        public BigDecimal getDecimal() throws RepositoryException {
            if(type == PropertyType.DECIMAL){
                return (BigDecimal) value;
            }
            throw new ValueFormatException("Value is not a Decimal: " + value.getClass().getSimpleName());
        }

        @Override
        public Calendar getDate() throws RepositoryException {
            if(type == PropertyType.DATE){
                return (Calendar) value;
            }
            throw new ValueFormatException("Value is not a Date: " + value.getClass().getSimpleName());
        }

        @Override
        public boolean getBoolean() throws RepositoryException {
            if(type == PropertyType.BOOLEAN) {
                return (Boolean) value;
            }
            throw new ValueFormatException("Value is not a Boolean: " + value.getClass().getSimpleName());
        }

        @Override
        public int getType() {
            return type;
        }
    }
}
