/*global kakao*/
import React, { useState } from "react";
import styled from "styled-components";
import { colorSchema, defaultValue, theme } from "lib/style";

const ButtonDesign = styled.div`
  border: 2px solid ${theme(4)};
  color: ${theme(4)};
  padding: 1rem 1.25rem;
  cursor: pointer;
  user-select: none;
  font-size: 0.75rem;
  & + & {
    margin-left: 1rem;
  }
  :hover {
    background: ${theme(4)};
    color: white;
  }
  :active {
    background: ${theme(6)};
    color: white;
  }
`;

function ControllButton({
  polygons,
  setPolygons,
  on,
  off,
  name,
  fileVal,
  setfileVal,
  map,
}) {
  const { tag } = defaultValue;

  const onClick = (name) => {
    setfileVal({
      ...fileVal,
      tag: name,
    });
    const filename = `${fileVal.fruit}_${fileVal.year}_${name}`;
    import(`res/${filename}`)
      .then((file) => {
        let data = file.features; // 지도 데이터
        let coordinates = []; // 좌표 배열

        const diplayArea = (coordinates, color) => {
          let path = [];
          let points = [];

          coordinates[0].forEach((coordinate) => {
            let point = {};
            point.x = coordinate[1];
            point.y = coordinate[0];
            points.push(point);
            path.push(new kakao.maps.LatLng(coordinate[1], coordinate[0]));
          });

          const polygon = new kakao.maps.Polygon({
            map: map,
            path: path,
            strokeWeight: 2,
            strokeColor: "#004c80",
            strokeOpacity: 0.8,
            strokeStyle: "solid",
            fillColor: color,
            fillOpacity: 0.5,
          });
          setPolygons((polygons) => [...polygons, polygon]);
          console.log(polygons);
        };

        // 데이터를 분리해서 그려주는 부분
        if (on !== name) {
          for (let i = 0; i < polygons.length; i++) {
            polygons[i].setMap(null);
          }
          data.forEach((val) => {
            coordinates = val.geometry.coordinates;
            diplayArea(coordinates, colorSchema[val.properties.DN]);
          });
          off(name);
        } else if (on === name) {
          for (let i = 0; i < polygons.length; i++) {
            polygons[i].setMap(null);
          }
          console.log(polygons);
          off(null);
        }
      })
      .catch((e) => {
        alert("년도와 과일을 확인해주세요");
        console.log(e);
      });
  };

  return (
    <ButtonDesign onClick={() => onClick(name)} name={name}>
      {tag[name]}
    </ButtonDesign>
  );
}

export default ControllButton;
