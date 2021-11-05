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
      <ControllButton name="rain" />
      <ControllButton name="mountain" />
      <ControllButton name="hello" />
      <ControllButton name="bye" />
    </Wrap>
  );
}

export default ButtonContainer;
