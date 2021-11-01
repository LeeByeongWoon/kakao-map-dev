/*global kakao*/
import OpenColor from "open-color";

export const colorSchema = {
  1: "#ff6b6b", //red
  2: "#cc5de8", // puple
  3: "#5c7cfa", // Indigo
  4: "#22b8cf", // Cyan
  5: "#fcc419", // yellow
};

export const theme = (weight) => {
  return OpenColor.indigo[weight];
};

// map style
export const options = {
  center: new kakao.maps.LatLng(37.54448747133563, 126.738295688373), // 지도의 중심 좌표
  level: 12, // 확대 레벨
};
