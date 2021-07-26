package org.starcoin.scan.utils;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import org.starcoin.bean.Struct;
import org.starcoin.bean.TypeTag;

import java.io.IOException;
import java.lang.reflect.Type;

public class TypeTagSerializer implements ObjectSerializer {

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType,
                      int features) throws IOException {
        if (object.getClass() == TypeTag.class) {
            Struct struct = ((TypeTag) object).getStruct();
            serializer.write(struct.getAddress() + "::" + struct.getModule() + "::" + struct.getName());
        } else {
            System.out.println("class not type tag: " + object.getClass());
        }
    }
}
