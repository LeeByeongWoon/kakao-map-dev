/*global kakao*/
import OpenColor from "open-color";
import React, { useState } from "react";
import styled from "styled-components";

const LatLngContain = styled.div`
  position: absolute;
  display: flex;
  left: 0.3rem;
  top: 0.3rem;
  z-index: 2;
`;
const Values = styled.div`
  font-size: 0.75rem;
  font-weight: bold;
  color: ${OpenColor.gray[7]};
  & + & {
    margin-left: 0.5rem;
  }
`;
function CenterChanged({ center }) {
  return (
    <LatLngContain>
      <Values>{center.Lat}</Values>
      <Values>{center.Lng}</Values>
    </LatLngContain>
  );
}

export default CenterChanged;
