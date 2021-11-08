import React from "react";
import styled from "styled-components";
import { theme } from "lib/style";

const ButtonDesign = styled.div`
  border: 2px solid ${theme(4)};
  color: ${theme(4)};
  padding: 1rem 1.25rem;
  cursor: pointer;
  user-select: none;
  font-size: 0.75rem;
  & + & {
    margin-left: 1rem;
  }
  :hover {
    background: ${theme(4)};
    color: white;
  }
  :active {
    background: ${theme(6)};
    color: white;
  }
`;
const onClick = (name) => {
  console.log(name + " onclick");
};
function ControllButton({ name }) {
  return (
    <ButtonDesign name={name} onClick={() => onClick(name)}>
      {name}
    </ButtonDesign>
  );
}

export default ControllButton;
