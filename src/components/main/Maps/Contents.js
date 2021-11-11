import React from "react";
import styled from "styled-components";
import { LegendContainer, Map } from ".";
import CenterChanged from "./CenterChanged";

const Position = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
`;

function Contents({ container, center, fileVal, title }) {
  return (
    <Position>
      <LegendContainer fileVal={fileVal} title={title} />
      <CenterChanged center={center} />
      <Map container={container} />
    </Position>
  );
}
export default Contents;
