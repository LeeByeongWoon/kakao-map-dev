import React from "react";
import styled from "styled-components";
import { ControllButton } from ".";

const Wrap = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: center;
  margin-top: 1rem;
`;
function ButtonContainer() {
  return (
    <Wrap>
      <ControllButton name="1월 평균 기온" />
      <ControllButton name="강수량" />
      <ControllButton name="연평균 기온" />
      <ControllButton name="1월 최저 기온" />
    </Wrap>
  );
}

export default ButtonContainer;
