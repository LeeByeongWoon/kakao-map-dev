/* global kakao */
import { colorSchema, options } from "lib/style";
import React, { useEffect, useRef, useState } from "react";
import geojson from "res/rain80.json";
import styled from "styled-components";

let data = geojson.features; // 지도 데아터
let coordinates = []; // 좌표 배열
let name = ""; // 이름(아이디)

let polygons = [];

const MapPosition = styled.div`
  height: 30rem;
  width: 100%;
`;

function Map() {
  const [map, setMap] = useState(null);
  const container = useRef(null); // 지도를 표시하는 DOM 객체

  useEffect(() => {
    const map = new kakao.maps.Map(container.current, options);
    map.addOverlayMapTypeId(kakao.maps.MapTypeId.USE_DISTRICT);

    const diplayArea = (coordinates, name, color) => {
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

      polygons.push(polygon);

      kakao.maps.event.addListener(polygon, "click", function (mouseEvent) {
        polygon.setOptions({ fillColor: "#fff" });
      });
    };

    // 데이터를 분리해서 그려주는 부분
    data.forEach((val) => {
      coordinates = val.geometry.coordinates;
      name = val.properties.fid;
      diplayArea(coordinates, name, colorSchema[val.properties.DN]);
    });
  }, []);

  //gps(https issue)
  // const locationLoadSuccess = (pos) => {
  //   let currentPos = new kakao.maps.LatLng(
  //     pos.coords.latitude,
  //     pos.coords.longtude
  //   );
  //   map.panTo(currentPos);
  // };
  // function locationLoadError(pos) {
  //   alert("error");
  // }
  // function getCurrentPosBtn() {
  //   navigator.geolocation.getCurrentPosition(
  //     locationLoadSuccess,
  //     locationLoadError
  //   );
  // }
  return (
    <div>
      <MapPosition ref={container} />
    </div>
  );
}

export default Map;
