import React from "react";
import styled from "styled-components";
import { LegendContainer, Map } from ".";

const Position = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
`;

function Contents({ container }) {
  return (
    <Position>
      <LegendContainer />
      <Map container={container} />
    </Position>
  );
}
export default Contents;
