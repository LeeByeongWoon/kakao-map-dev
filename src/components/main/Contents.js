import { colorSchema } from "lib/style";
import React from "react";
import styled from "styled-components";
import Map from "./Map";

const Position = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
`;

const LegendContainer = styled.div`
  position: absolute;
  right: 0.3rem;
  top: 0.3rem;
  z-index: 2;
`;
const LegendBox = styled.div`
  background: ${(props) => colorSchema[props.color]};
  font-size: 0.8rem;
  color: white;
  padding: 0.2rem 0.5rem;
`;

function Contents() {
  return (
    <Position>
      <LegendContainer>
        <LegendBox color="1">1급</LegendBox>
        <LegendBox color="2">2급</LegendBox>
        <LegendBox color="3">3급</LegendBox>
        <LegendBox color="4">4급</LegendBox>
        <LegendBox color="5">5급</LegendBox>
      </LegendContainer>
      <Map />
    </Position>
  );
}
export default Contents;
