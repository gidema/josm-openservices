package nl.gertjanidema.conversion.valuemapper;

import java.text.ParseException;

public class FloatValueMapper extends NumberValueMapper<Float> {

    @Override
    public Float parse(String source) throws ValueMapperException {
        if (isNull(source)) {
            return null;
        }
        try {
            return format.parse(source).floatValue();
        } catch (ParseException e) {
            throw new ValueMapperException("Invalid number format", e);
        }
    }

    @Override
    protected String typedFormat(Float object) throws ValueMapperException {
        if (object == null) {
            return super.typedFormat(object);
        }
        return format.format(object);
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

}
