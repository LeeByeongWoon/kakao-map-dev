/*global kakao */
import { colorSchema } from "lib/style";
import React, { useState } from "react";
import styled from "styled-components";
import { ControllButton } from ".";

const Wrap = styled.form`
  display: flex;
  flex-direction: row;
  justify-content: center;
  margin-top: 1rem;
`;
function ButtonContainer({ fileVal, setfileVal, map }) {
  const [input, setInput] = useState("");
  const [on, off] = useState("");
  const [polygons, setPolygons] = useState([]);

  const onSubmit = (e) => {
    e.preventDefault();
    const filename = input;
    import(`res/${filename}`)
      .then((file) => {
        console.log(file);
        let data = file.features; // 지도 데이터
        let coordinates = []; // 좌표 배열
        let name = ""; // 이름(아이디)
        let polygons = [];
        let polygon;
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

          polygon = new kakao.maps.Polygon({
            map: map,
            path: path,
            strokeWeight: 2,
            strokeColor: "#004c80",
            strokeOpacity: 0.8,
            strokeStyle: "solid",
            fillColor: color,
            fillOpacity: 0.5,
          });
          polygons.push(polygon);
        };

        // 데이터를 분리해서 그려주는 부분
        data.forEach((val) => {
          coordinates = val.geometry.coordinates;
          diplayArea(coordinates, colorSchema[val.properties.DN]);
        });
      })
      .catch((e) => {
        alert("년도와 과일을 확인해주세요");
        console.log(e);
      });
  };
  return (
    <Wrap onSubmit={onSubmit}>
      <ControllButton
        name="average"
        fileVal={fileVal}
        setfileVal={setfileVal}
        map={map}
        on={on}
        off={off}
        polygons={polygons}
        setPolygons={setPolygons}
      />
      <ControllButton
        name="rain"
        fileVal={fileVal}
        setfileVal={setfileVal}
        map={map}
        on={on}
        off={off}
        polygons={polygons}
        setPolygons={setPolygons}
      />
      <ControllButton
        name="totalAverage"
        fileVal={fileVal}
        setfileVal={setfileVal}
        map={map}
        on={on}
        off={off}
        polygons={polygons}
        setPolygons={setPolygons}
      />
      <ControllButton
        name="minTemp"
        fileVal={fileVal}
        setfileVal={setfileVal}
        map={map}
        on={on}
        off={off}
        polygons={polygons}
        setPolygons={setPolygons}
      />
    </Wrap>
  );
}

export default ButtonContainer;
