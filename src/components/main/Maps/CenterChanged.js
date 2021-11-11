import OpenColor from "open-color";
import React from "react";
import styled from "styled-components";

const LatLngContain = styled.div`
  position: absolute;
  display: flex;
  flex-direction: column;
  left: 0.3rem;
  top: 0.3rem;
  z-index: 2;
`;
const Values = styled.div`
  font-size: 0.75rem;
  font-weight: bold;
  color: ${OpenColor.gray[7]};
`;
function CenterChanged({ center }) {
  return (
    <LatLngContain>
      <Values>위도: {center.Lat.toFixed(8)}</Values>
      <Values>경도: {center.Lng.toFixed(7)}</Values>
    </LatLngContain>
  );
}

export default CenterChanged;
