import React from "react";
import styled from "styled-components";
import { ButtonContainer } from "./Btns";
import { DropdownMenu } from "./DropDown";
import { Contents } from "./Maps";

const Wrapper = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
`;
function MainContainer({ map, container }) {
  return (
    <Wrapper>
      <DropdownMenu />
      <Contents container={container} />
      <ButtonContainer map={map} />
    </Wrapper>
  );
}

export default MainContainer;
