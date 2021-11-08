import { theme } from "lib/style";
import oc from "open-color";
import React, { useEffect, useState } from "react";
import { IoIosArrowUp } from "react-icons/io";
import styled, { css } from "styled-components";

const BtnContainer = styled.div`
  & + & {
    margin-left: 0.5rem;
  }
  position: relative;
  display: flex;
  flex-direction: row;
  border: 2px solid ${oc.gray[5]};
  border-radius: 5px;
  padding: 0.5rem;
  ${(props) =>
    props.isActive &&
    css`
      border: 2px solid ${theme(5)};
    `} {
  }
`;

const TextBox = styled.input`
  border: none;
  user-select: none;
  -webkit-user-select: none;
  -webkit-user-drag: none;

  cursor: pointer;
  :focus {
    outline: none;
  }
`;
const DropDownArrowBtn = styled.div`
  display: flex;
  align-content: center;
  justify-content: center;
  border-radius: 50px;
  background: ${oc.gray[5]};
  padding: 0.125rem;
  color: white;

  ${(props) =>
    props.isActive &&
    css`
      background: ${theme(3)};
      transform: rotate(180deg);
    `}
`;

const OptionList = styled.ul`
  background: white;
  position: absolute;
  z-index: 999;
  top: 100%;
  left: 0;
  width: 100%;
  overflow: scroll;
  margin-top: 0.2rem;
  border: 1px solid ${oc.gray[2]};
  ${(props) =>
    props.isActive &&
    css`
      display: none;
    `}
  li {
    user-select: none;
    cursor: pointer;
    padding: 0.5rem 0.3rem;
    :hover {
      background: ${theme(3)};
      color: white;
    }
    :active {
      background: ${theme(5)};
    }
  }
`;

function DropdownBtn({ data, name }) {
  const [isActive, getActive] = useState(false);
  const [select, selected] = useState("");
  useEffect(() => {
    getActive(false);
    selected("");
  }, []);

  const onClick = (e) => {
    getActive((prev) => !prev);
  };

  return (
    <BtnContainer onClick={() => onClick()} isActive={isActive}>
      <TextBox readOnly value={select} placeholder="선택해주세요" />
      <DropDownArrowBtn isActive={isActive}>
        <IoIosArrowUp />
      </DropDownArrowBtn>
      <OptionList isActive={!isActive}>
        {data.map((tag) => (
          <li
            onClick={(e) => {
              selected(tag);
              console.log(e.target.value);
            }}
            value={tag}>
            {tag}
          </li>
        ))}
      </OptionList>
    </BtnContainer>
  );
}

export default DropdownBtn;
