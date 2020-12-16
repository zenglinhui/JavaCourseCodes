package io.kimmking.rpcfx.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

public class XStreamUtils {

    private static XStream stream;

    public static XStream createToJson() {
        stream = new XStream(new JettisonMappedXmlDriver());
        stream.setMode(XStream.NO_REFERENCES);
        return stream;
    }

    public static <T> T fromBean(XStream stream, String str) {
        if (null != stream) {
            return (T) stream.fromXML(str);
        }
        return null;
    }

    public static String to(XStream stream, Object obj) {
        if (null != stream) {
            return stream.toXML(obj);
        }
        return null;
    }


    public static XStream createToXml() {
        stream = new XStream();
        return stream;
    }

}
