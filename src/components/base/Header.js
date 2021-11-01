import oc from "open-color";
import React from "react";
import styled from "styled-components";
import InputAdr from "./InputAdr";

const Positioner = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
`;
//위치 잡기
const Background = styled.div`
  background: white;
  display: flex;
  justify-content: center;
  height: auto;
  flex-direction: column;
  padding-bottom: 1rem;
`;
// 배경
const Logo = styled.div`
  display: flex;
  flex-flow: column;
  color: ${oc.gray[6]};
  user-select: none;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  margin: 1.25rem 1rem;
`;
//logo

function Header() {
  return (
    <Positioner>
      <Background>
        <Logo>Title</Logo>
        <InputAdr />
      </Background>
    </Positioner>
  );
}

export default Header;
