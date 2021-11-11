import { colorSchema, defaultValue } from "lib/style";
import React, { useEffect, useState } from "react";
import styled, { css } from "styled-components";
import oc from "open-color";

const CommentContain = styled.div`
  position: absolute;
  right: 3rem;
  top: 0.3rem;
  z-index: 3;
`;
const Comment = styled.div`
  display: block;
  font-size: 0.8rem;
  color: ${oc.gray[7]};
  padding: 0.5rem 0.3rem;
  max-width: 10rem;
  opacity: 0.8;
  background: ${oc.gray[2]};
  white-space: pre-wrap;
  ${(props) =>
    !props.active &&
    css`
      display: none;
    `}
`;
const CommentText = styled.div`
  display: flex;
`;
const Title = styled.div`
  width: 100%;
  text-align: center;
`;
const LegendContain = styled.div`
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
  user-select: none;
`;

function LegendContainer({ title }) {
  const [active, isActive] = useState(false);

  const onMouseOver = () => {
    isActive(true);
  };
  const onMouseOut = () => {
    isActive(false);
  };

  return (
    <>
      {title != "" && (
        <CommentContain>
          <Comment active={active}>
            <Title>{title}</Title>
            <CommentText>
              1급: <p>-3℃</p>
            </CommentText>
            <CommentText>
              2급: <p>-4℃</p>
            </CommentText>
            <CommentText>
              3급: <p>-5℃</p>
            </CommentText>
            <CommentText>
              4급: <p>-6℃</p>
            </CommentText>
            <CommentText>
              5급: <p>-7℃</p>
            </CommentText>
          </Comment>
        </CommentContain>
      )}
      <LegendContain
        onMouseEnter={() => onMouseOver()}
        onMouseOut={() => onMouseOut()}>
        <LegendBox color="1">1급</LegendBox>
        <LegendBox color="2">2급</LegendBox>
        <LegendBox color="3">3급</LegendBox>
        <LegendBox color="4">4급</LegendBox>
        <LegendBox color="5">5급</LegendBox>
      </LegendContain>
    </>
  );
}

export default LegendContainer;
