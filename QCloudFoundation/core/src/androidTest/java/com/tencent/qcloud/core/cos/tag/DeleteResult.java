package com.tencent.qcloud.core.cos.tag;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

/**
 * Created by bradyxiao on 2017/5/23.
 * author bradyxiao
 */
@XStreamAlias("DeleteResult")
public class DeleteResult {
    /**
     * Indicate the information of Object that has been deleted successfully
     */
    @XStreamImplicit(itemFieldName ="Deleted")
    public List<Deleted> deleteds;

    /**
     * Indicate the information of Object that failed to be deleted
     */
    @XStreamImplicit(itemFieldName ="Error")
    public List<Error> errors;

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[DeleteResult");
        if(deleteds != null){
            int size = deleteds.size();
            for(int i = 0; i < size; ++ i){
                stringBuilder.append(deleteds.get(i).toString()).append("\n");
            }
        }
        if(errors != null){
            int size = errors.size();
            for(int i = 0; i < size; ++ i){
                stringBuilder.append(errors.get(i).toString()).append("\n");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
