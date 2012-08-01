package org.apache.vxquery.runtime.functions.cast;

import java.io.DataOutput;
import java.io.IOException;

import org.apache.vxquery.datamodel.accessors.atomic.XSDatePointable;
import org.apache.vxquery.datamodel.accessors.atomic.XSDateTimePointable;
import org.apache.vxquery.datamodel.util.DateTime;
import org.apache.vxquery.datamodel.values.ValueTag;
import org.apache.vxquery.exceptions.ErrorCode;
import org.apache.vxquery.exceptions.SystemException;
import org.apache.vxquery.runtime.functions.strings.ICharacterIterator;
import org.apache.vxquery.runtime.functions.strings.UTF8StringCharacterIterator;

import edu.uci.ics.hyracks.data.std.primitive.UTF8StringPointable;

public class CastToDateOperation extends AbstractCastToOperation {

    @Override
    public void convertDate(XSDatePointable datep, DataOutput dOut) throws SystemException, IOException {
        dOut.write(ValueTag.XS_DATE_TAG);
        dOut.write(datep.getByteArray(), datep.getStartOffset(), datep.getLength());
    }

    @Override
    public void convertDatetime(XSDateTimePointable datetimep, DataOutput dOut) throws SystemException, IOException {
        dOut.write(ValueTag.XS_DATE_TAG);
        dOut.writeShort((short) datetimep.getYear());
        dOut.write((byte) datetimep.getMonth());
        dOut.write((byte) datetimep.getDay());
        dOut.write((byte) datetimep.getTimezoneHour());
        dOut.write((byte) datetimep.getTimezoneMinute());
    }

    @Override
    public void convertString(UTF8StringPointable stringp, DataOutput dOut) throws SystemException, IOException {
        ICharacterIterator charIterator = new UTF8StringCharacterIterator(stringp);
        charIterator.reset();
        int c;
        int index = 0;
        long[] date = new long[5];
        boolean positiveTimezone = false;

        // Set defaults
        date[3] = DateTime.TIMEZONE_HOUR_NULL;
        date[4] = DateTime.TIMEZONE_MINUTE_NULL;

        while ((c = charIterator.next()) != ICharacterIterator.EOS_CHAR) {
            if (Character.isDigit(c)) {
                date[index] = date[index] * 10 + Character.getNumericValue(c);
            } else if (c == Character.valueOf('-') || c == Character.valueOf(':')) {
                ++index;
            } else if (c == Character.valueOf('+')) {
                positiveTimezone = true;
                ++index;
            } else {
                // Invalid date format.
                throw new SystemException(ErrorCode.FORG0001);
            }
        }
        // Final touches on timezone.
        if (!positiveTimezone && date[3] != DateTime.TIMEZONE_HOUR_NULL) {
            date[3] *= -1;
        }
        if (!positiveTimezone && date[4] != DateTime.TIMEZONE_MINUTE_NULL) {
            date[4] *= -1;
        }
        // Double check for a valid date
        if (!DateTime.valid(date[0], date[1], date[2], 0, 0, 0, date[3], date[4])) {
            throw new SystemException(ErrorCode.FODT0001);
        }

        dOut.write(ValueTag.XS_DATE_TAG);
        dOut.writeShort((short) date[0]);
        dOut.write((byte) date[1]);
        dOut.write((byte) date[2]);
        dOut.write((byte) date[3]);
        dOut.write((byte) date[4]);
    }

    @Override
    public void convertUntypedAtomic(UTF8StringPointable stringp, DataOutput dOut) throws SystemException, IOException {
        convertString(stringp, dOut);
    }
}