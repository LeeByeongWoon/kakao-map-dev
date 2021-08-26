import React, { memo, useEffect, useState } from "react";

import Select from "react-select";

import {
    Row,
    Col,
    Label,
    Input,
    Form,
    FormGroup
  } from "reactstrap";

  //Air, Farm, Factory, Bio, Life, Energy, Weather, City, Traffic, Culture, Economy
const mainDomain = [
    { index: 0, value: "Air", label: "Air" },
    { index: 1, value: "Farm", label: "Farm" },
    { index: 2, value: "Factory", label: "Factory" },
    { index: 3, value: "Bio", label: "Bio" },
    { index: 4, value: "Life", label: "Life" },
    { index: 5, value: "Energy", label: "Energy" },
    { index: 6, value: "Weather", label: "Weather" },
    { index: 7, value: "City", label: "City" },
    { index: 8, value: "Traffic", label: "Traffic" },
    { index: 9, value: "Culture", label: "Culture" },
    { index: 10, value: "Economy", label: "Economy" }
]


const BasicInfomation = (props) => {
    const { commitData } = props;

    const commitColumns = commitData[0];

    const [mainDomainSelect, setMainDomainSelect] = useState(null);
    const [timestampSelect, setTimestampSelect] = useState(null);

    return (
        <>
            <Form className="form-horizontal">
                <Row>
                    <Label md="2">Main domain</Label>
                    <Col md="4">
                        <FormGroup>
                            <Select
                            className="react-select primary"
                            classNamePrefix="react-select"
                            name="mainDomainSelect"
                            value={mainDomainSelect}
                            onChange={(value) => setMainDomainSelect(value)}
                            options={mainDomain}
                            placeholder="option"
                            />
                        </FormGroup>
                    </Col>
                </Row>
                <Row>
                    <Label md="2">Sub domain</Label>
                    <Col md="4">
                        <FormGroup>
                            <Input placeholder="" type="text" />
                        </FormGroup>
                    </Col>
                </Row>
                <Row>
                    <Label md="2">Table name</Label>
                    <Col md="4">
                        <FormGroup>
                            <Input placeholder="" type="text" />
                        </FormGroup>
                    </Col>
                </Row>
                <Row>
                    <Label md="2">Timestamp column</Label>
                    <Col md="4">
                        <FormGroup>
                            <Select
                            className="react-select primary"
                            classNamePrefix="react-select"
                            name="timestampSelect"
                            value={timestampSelect}
                            onChange={(value) => setTimestampSelect(value)}
                            options={commitColumns}
                            placeholder="option"
                            />
                        </FormGroup>
                    </Col>
                </Row>
            </Form>
        </>
    );
}

export default memo(BasicInfomation);
