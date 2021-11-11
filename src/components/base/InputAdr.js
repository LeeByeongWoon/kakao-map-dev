/*global kakao*/
import { theme } from "lib/style";
import oc from "open-color";
import React, { useState } from "react";
import styled from "styled-components";

//위치잡기
const Position = styled.form`
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
  @media only screen and(max-width: 768px) {
    width: 100%;
  }
`;
//주소검색
const Search = styled.div`
  display: flex;
  align-items: center;
  padding: 0 1rem;

  color: ${theme(4)};
  border: 1px solid ${theme(4)};

  text-align: center;
  font-size: 1rem;
  font-weight: 500;

  cursor: pointer;
  user-select: none;

  :hover {
    background: ${theme(4)};
    color: white;
  }
  &:active {
    background: ${theme(6)};
    color: white;
  }
`;

function InputAdr({ map }) {
  const [input, setInput] = useState("");
  const [markers, setMarkers] = useState([]);
  let place = new kakao.maps.services.Places();

  const onChange = (e) => {
    setInput(e.target.value);
  };

  const searchAddr = () => {
    if (!input.replace(/^\s+|\s+$/g, "")) {
      alert("키워드를 입력해주세요!");
      return false;
    }

    place.keywordSearch(input, placeSearchCB);
  };
  const placeSearchCB = (data, status, pagination) => {
    if (status === kakao.maps.services.Status.OK) {
      let bounds = new kakao.maps.LatLngBounds();
      let infowindow = new kakao.maps.InfoWindow({ zIndex: 1 });

      if (markers.length !== 0) {
        for (let i = 0; i < markers.length; i++) {
          markers[i].setMap(null);
        }
      }
      infowindow.close();

      for (let i = 0; i < data.length; i++) {
        let marker = new kakao.maps.Marker({
          map: map,
          position: new kakao.maps.LatLng(data[i].y, data[i].x),
        });
        setMarkers((markers) => [...markers, marker]);

        kakao.maps.event.addListener(marker, "click", function () {
          infowindow.setContent(
            `<div style="padding:5px;"> ${data[i].place_name}</div>`
          );
          infowindow.open(map, marker);
        });

        bounds.extend(new kakao.maps.LatLng(data[i].y, data[i].x));
      }
      console.log(data);
      map.setBounds(bounds);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    searchAddr();
  };
  return (
    <Position onSubmit={handleSubmit}>
      <Input
        onChange={onChange}
        value={input}
        placeholder="주소를 입력해 주세요"></Input>
      <Search onClick={searchAddr}>검색</Search>
    </Position>
  );
}

export default InputAdr;
