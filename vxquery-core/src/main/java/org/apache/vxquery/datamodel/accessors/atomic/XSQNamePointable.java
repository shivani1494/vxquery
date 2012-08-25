package org.apache.vxquery.datamodel.accessors.atomic;

import edu.uci.ics.hyracks.api.dataflow.value.ITypeTraits;
import edu.uci.ics.hyracks.data.std.api.AbstractPointable;
import edu.uci.ics.hyracks.data.std.api.IPointable;
import edu.uci.ics.hyracks.data.std.api.IPointableFactory;
import edu.uci.ics.hyracks.data.std.primitive.UTF8StringPointable;

public class XSQNamePointable extends AbstractPointable {
    public static final ITypeTraits TYPE_TRAITS = new ITypeTraits() {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isFixedLength() {
            return false;
        }

        @Override
        public int getFixedLength() {
            return 0;
        }
    };

    public static final IPointableFactory FACTORY = new IPointableFactory() {
        private static final long serialVersionUID = 1L;

        @Override
        public IPointable createPointable() {
            return new XSQNamePointable();
        }

        @Override
        public ITypeTraits getTypeTraits() {
            return TYPE_TRAITS;
        }
    };

    public int getUriLength() {
        return getUriLength(bytes, start);
    }

    public static int getUriLength(byte[] bytes, int start) {
        return getUriUTFLength(bytes, start) + 2;
    }

    public int getUriUTFLength() {
        return getUriUTFLength(bytes, start);
    }

    public static int getUriUTFLength(byte[] bytes, int start) {
        return UTF8StringPointable.getUTFLength(bytes, start);
    }

    public int getPrefixLength() {
        return getPrefixLength(bytes, start);
    }

    public static int getPrefixLength(byte[] bytes, int start) {
        return getPrefixUTFLength(bytes, start) + 2;
    }

    public int getPrefixUTFLength() {
        return getPrefixUTFLength(bytes, start);
    }

    public static int getPrefixUTFLength(byte[] bytes, int start) {
        return UTF8StringPointable.getUTFLength(bytes, start + getUriLength(bytes, start));
    }

    public int getLocalNameLength() {
        return getLocalNameLength(bytes, start);
    }

    public static int getLocalNameLength(byte[] bytes, int start) {
        return getLocalNameUTFLength(bytes, start) + 2;
    }

    public int getLocalNameUTFLength() {
        return getLocalNameUTFLength(bytes, start);
    }

    public static int getLocalNameUTFLength(byte[] bytes, int start) {
        return UTF8StringPointable.getUTFLength(bytes,
                start + getUriLength(bytes, start) + getPrefixLength(bytes, start));
    }

    public void getUri(IPointable stringp) {
        getUri(bytes, start, stringp);
    }

    public static void getUri(byte[] bytes, int start, IPointable stringp) {
        stringp.set(bytes, start, getUriLength(bytes, start));
    }

    public void getPrefix(IPointable stringp) {
        getPrefix(bytes, start, stringp);
    }

    public static void getPrefix(byte[] bytes, int start, IPointable stringp) {
        stringp.set(bytes, start + getUriLength(bytes, start), getPrefixLength(bytes, start));
    }

    public void getLocalName(IPointable stringp) {
        getLocalName(bytes, start, stringp);
    }

    public static void getLocalName(byte[] bytes, int start, IPointable stringp) {
        stringp.set(bytes, start + getUriLength(bytes, start) + getPrefixLength(bytes, start),
                getLocalNameLength(bytes, start));
    }
}