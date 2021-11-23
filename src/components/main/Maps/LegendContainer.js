import { legendText } from "lib/LegendComment";
import { colorSchema, defaultValue } from "lib/style";
import oc from "open-color";
import React, { useState } from "react";
import styled, { css } from "styled-components";

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
  margin-bottom: 0.2rem;
`;
const LegendContain = styled.div`
  position: absolute;
  right: 0.3rem;
  top: 0.3rem;
  z-index: 2;
`;
const LegendBox = styled.div`
  background: ${(props) => colorSchema[props.color]};
  font-size: 0.5rem;
  color: white;
  padding: 0.2rem 0.5rem;
  user-select: none;
  text-align: center;
`;
function LegendContainer({ title, tag, fileVal }) {
  const [active, isActive] = useState(false);
  const { tag: tags } = defaultValue;
  const onMouseOver = () => {
    isActive(true);
  };
  const onMouseOut = () => {
    isActive(false);
  };

  return (
    <>
      {tag && (
        <CommentContain>
          <Comment active={active}>
            <Title>
              {title}
              &nbsp;
              {tags[tag]}
            </Title>
            <CommentText>
              매우적합: <p>{legendText[fileVal.fruit][tag][0]}</p>
            </CommentText>
            <CommentText>
              적합: <p>{legendText[fileVal.fruit][tag][1]}</p>
            </CommentText>
            <CommentText>
              부정교합: <p>{legendText[fileVal.fruit][tag][2]}</p>
            </CommentText>
          </Comment>
        </CommentContain>
      )}
      <LegendContain
        onMouseEnter={() => onMouseOver()}
        onMouseOut={() => onMouseOut()}>
        <LegendBox color="1">매우적합</LegendBox>
        <LegendBox color="2">적합</LegendBox>
        <LegendBox color="3">부정교합</LegendBox>
      </LegendContain>
    </>
  );
}

export default LegendContainer;
