/*global kakao*/
import React, { useState } from "react";
import styled from "styled-components";
import oc from "open-color";

//위치잡기
const Position = styled.div`
  display: flex;
  height: auto;
  justify-content: center;
`;
//주소 검색창
const Input = styled.input`
  width: 50%;
  border: 1px solid ${oc.gray[5]};
  outline: none;
  line-height: 2.5rem;
  font-size: 1.2rem;
  margin-right: 0.2rem;

  padding-left: 0.5rem;
  padding-right: 0.5rem;

  color: ${oc.gray[7]};
  ::placeholder {
    color: ${oc.gray[3]};
  }
`;
//주소검색
const Search = styled.div`
  display: flex;
  align-items: center;
  padding: 0 1rem;

  color: ${oc.indigo[4]};
  border: 1px solid ${oc.indigo[4]};

  text-align: center;
  font-size: 1rem;
  font-weight: 500;

  cursor: pointer;
  user-select: none;

  &:active {
    background: ${oc.indigo[4]};
    color: white;
  }
`;
function InputAdr() {
  const [input, setInput] = useState("");

  const onChange = (e) => {
    setInput(e.target.value);
  };

  const searchAddr = () => {
    console.log(input);
    // let place = new kakao.maps.service.Places();
    // place.keywordSearch(input, placeSearchCB);
  };
  // const placeSearchCB = (data, status, pagination) => {
  //   if (status === kakao.maps.services.Status.OK) {
  //     let bounds = new kakao.maps.LatLngBounds();

  //     for (let i = 0; i < data.length; i++) {
  //       bounds.extend(new kakao.maps.LatLng(data[i].y, data[i].x));
  //     }
  //     map.setBounds(bounds);
  //   }
  // };
  return (
    <Position>
      <Input
        onChange={onChange}
        value={input}
        placeholder="주소를 입력해 주세요"></Input>
      <Search onClick={searchAddr}>검색</Search>
    </Position>
  );
}

export default InputAdr;
