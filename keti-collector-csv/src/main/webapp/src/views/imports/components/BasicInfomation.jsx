import React, { memo, useEffect, useState } from "react";

import axios from "axios";

import Select from "react-select";

import {
    ProgressBar
} from "react-bootstrap"

import {
    Row,
    Col,
    Card,
    CardHeader,
    CardTitle,
    CardBody,
    Label,
    Input,
    Button,
    Form,
    FormGroup
  } from "reactstrap";

//Air, Farm, Factory, Bio, Life, Energy, Weather, City, Traffic, Culture, Economy
const defaultMainDomain = [
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
];


const BasicInfomation = ({ files, rules }) => {
    const { columns, rows } = rules;

    const [mainDomain, setMainDomain] = useState({});
    const [subDomain, setSubDomain] = useState(null);
    const [timeIndex, setTimeIndex] = useState({});
    const [measurement, setMeasurement] = useState({});

    const [functionRules, setFunctionRules] = useState(
        [
            {"index": 0, "column": "", "value": "", "func": ""}
        ]
    );

    const [progress, setProgress] = useState(0);


    const handleOnFileUpload = async (files) => {
        const params = {
            ...rules,
            database: mainDomain.value + "__" + subDomain,
            measurement: measurement,
            timeIndex: timeIndex
        };

        // try {
        //     const url = "/api/files";
        //     const method= "POST";
        //     const headers = {
        //         "Content-Type": "multipart/form-data"
        //     };

        //     const formData = new FormData();
        //     formData.append("files", files[0]);
        //     formData.append("params", JSON.stringify(params));

        //     const response = await axios({
        //         url: url,
        //         method: method,
        //         headers: headers,
        //         data: formData,
        //         onUploadProgress: (e) => {
        //             const progressRate = (e.loaded / e.total * 100).toFixed(1);
        //             setProgress(progressRate);
        //         },
        //     });

        // } catch (error) {
        //     console.error(error);
        // }
    }

    return (
        <>
            <Card>

                <CardHeader>
                    <CardTitle tag="h4">1. Basic Infomation</CardTitle>
                </CardHeader>
                <CardBody>
                    <Form className="form-horizontal">
                        <Row>
                            <Label md="2">Main domain</Label>
                            <Col md="3">
                                <FormGroup>
                                    <Select
                                        className="react-select primary"
                                        classNamePrefix="react-select"
                                        name="mainDomain"
                                        value={mainDomain}
                                        onChange={ v => setMainDomain(v) }
                                        options={defaultMainDomain}
                                        placeholder="option"
                                        />
                                </FormGroup>
                            </Col>
                            <Label
                                md="2"
                                style={{
                                    width: "140px"
                                }}
                                >
                                    Database
                            </Label>
                            <Col md="3">
                                <FormGroup>
                                    <Input
                                        readOnly
                                        placeholder=""
                                        type="text"
                                        onChange={() => {}}
                                        value={
                                            mainDomain.value !== null && subDomain !== null
                                            ?
                                                mainDomain.value + "__" + subDomain
                                            :
                                            ""
                                        }
                                        />
                                        
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row>
                            <Label md="2">Sub domain</Label>
                            <Col md="3">
                                <FormGroup>
                                    <Input
                                        placeholder=""
                                        type="text"
                                        onChange={ e => setSubDomain(e.target.value) }
                                        value={subDomain}
                                        />
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row>
                            <Label md="2">Time index column</Label>
                            <Col md="3">
                                <FormGroup>
                                    <Select
                                        className="react-select primary"
                                        classNamePrefix="react-select"
                                        name="timeIndex"
                                        value={timeIndex}
                                        onChange={ v => setTimeIndex(v) }
                                        options={ rules.columns !== undefined ? columns : [] }
                                        placeholder="option"
                                        />
                                </FormGroup>
                            </Col>
                            <Label
                                md="2"
                                style={{
                                    width: "140px"
                                }}
                                >
                                    Time index format
                            </Label>
                            <Col md="3">
                                <FormGroup>
                                    <Input
                                        placeholder=""
                                        type="text"
                                        // onChange={ e => setSubDomain({...subDomain, "value": e.target.value}) }
                                        // value={subDomain.value}
                                        />
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row>
                            <Label md="2">Measurement</Label>
                            <Col md="2">
                                <FormGroup>
                                    <div
                                        style={{ marginTop: "8px" }}
                                        className="form-check-radio"
                                        >
                                            <Label check>
                                                <Input
                                                    defaultValue="user"
                                                    id="userCreate"
                                                    name="measurement"
                                                    type="radio"
                                                    onClick={ e => setMeasurement({...measurement, "radio": "user"}) }
                                                    />
                                                        user-generated <span className="form-check-sign" />
                                            </Label>
                                    </div>
                                </FormGroup>
                            </Col>
                            <Col md="2">
                                <FormGroup>
                                    <div
                                        style={{ marginTop: "8px" }}
                                        className="form-check-radio"
                                        >
                                            <Label check>
                                                <Input
                                                    defaultValue="auto"
                                                    id="autoCreate"
                                                    name="measurement"
                                                    type="radio"
                                                    onClick={ e => setMeasurement({...measurement, "radio": "auto"}) }
                                                    />
                                                        auto-generated <span className="form-check-sign" />
                                            </Label>
                                    </div>
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row>
                            <Label md="2"></Label>
                            <Col md="3">
                                <FormGroup 
                                    style={{
                                        display: measurement.radio !== "user" ? "none" : "block"
                                    }}
                                    >
                                        <Input
                                            placeholder=""
                                            type="text"
                                            onChange={ e => setMeasurement({...measurement, "value": e.target.value}) }
                                            value={measurement.value}
                                            />
                                </FormGroup>
                                <FormGroup
                                    style={{
                                        display: measurement.radio !== "auto" ? "none" : "block"
                                    }}
                                    >
                                        <Select
                                            className="react-select primary"
                                            classNamePrefix="react-select"
                                            name="measurement"
                                            value={measurement}
                                            onChange={ e => setMeasurement({ ...measurement, ...e }) }
                                            options={ rules.columns !== undefined ? columns : [] }
                                            placeholder="option"
                                            />
                                </FormGroup>
                            </Col>
                        </Row>
                    </Form>
                </CardBody>

                <CardHeader>
                    <CardTitle tag="h4">2. Data Selection</CardTitle>
                </CardHeader>
                <CardBody>
                    {
                        functionRules.map((v, i) => {
                            const { index, column, value, func } = v;
                            return (
                                <>
                                    <Row key={"functionRules" + i + "_" + v}>
                                        <Col md="3">
                                            <Form action="#" method="#">
                                                <label>Column_{index+1}</label>
                                                <FormGroup>
                                                    <Input placeholder={column} type="email" />
                                                </FormGroup>
                                            </Form>
                                        </Col>
                                        <Col md="3">
                                            <Form action="#" method="#">
                                                <label>Value_{index+1}</label>
                                                <FormGroup>
                                                    <Input placeholder={value} type="email" />
                                                </FormGroup>
                                            </Form>
                                        </Col>
                                        <Col md="3">
                                            <Form action="#" method="#">
                                                <label>Function_{index+1}</label>
                                                <FormGroup>
                                                    <Input placeholder={func} type="email" />
                                                </FormGroup>
                                            </Form>
                                        </Col>
                                    </Row>
                                </>
                            )
                        })
                    }
                    <Row>
                        <Col
                            md="9"
                            style={{
                                "textAlign": "right",
                                "paddingTop": 0
                            }}
                            >
                                <Button
                                    color="default"
                                    size="sm"
                                    onClick={
                                        () => {
                                            const temp = [...functionRules];
                                            temp.push({"index": temp.length, "column": "", "value": "", "func": ""});

                                            setFunctionRules(temp);
                                        }
                                    }
                                    >
                                        <i className="nc-icon nc-simple-add" />
                                </Button>
                                <Button
                                    color="default"
                                    size="sm"
                                    onClick={
                                        () => {
                                            const temp = [...functionRules];
                                            temp.splice(temp.length-1, 1);

                                            setFunctionRules(temp);
                                        }
                                    }
                                    >
                                        <i className="nc-icon nc-simple-delete" />
                                </Button>
                        </Col>
                    </Row>
                    <Row>
                        <Col
                            md="12"
                            style={{
                                "textAlign": "right",
                                "paddingTop": 0
                            }}
                            >
                                {
                                    progress !== 0
                                    ?
                                        <>
                                            <ProgressBar
                                                now={progress}
                                                label={progress + "%"}
                                                />
                                        </>
                                    :
                                        <>
                                            <Button
                                                color="primary"
                                                onClick={ () => files.length !==0 ? handleOnFileUpload(files) : null }
                                                >
                                                    commit
                                            </Button>
                                        </>
                                }
                        </Col>
                    </Row>        
                </CardBody>

            </Card>
        </>
    );
}

export default memo(BasicInfomation);