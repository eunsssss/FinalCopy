package com.spring.boot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spring.boot.dto.MeetDTOYj;

@Mapper
public interface MeetMapperYj {

    public List<MeetDTOYj> getLists() throws Exception;

    //public void insertData(MeetDTOYj dto) throws Exception;

}
