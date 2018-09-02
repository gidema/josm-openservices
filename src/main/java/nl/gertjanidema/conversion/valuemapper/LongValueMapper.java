package nl.gertjanidema.conversion.valuemapper;

import java.text.ParseException;

// TODO implement octal, hex, radix
public class LongValueMapper extends NumberValueMapper<Long> {

    @Override
    public Long parse(String source) throws ValueMapperException {
        if (isNull(source)) {
            return null;
        }
        try {
            return format.parse(source).longValue();
        } catch (ParseException e) {
            throw new ValueMapperException("Invalid number format", e);
        }
    }

    @Override
    protected String typedFormat(Long object) throws ValueMapperException {
        if (object == null) {
            return super.typedFormat(object);
        }
        return format.format(object);
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

}
