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
    { value: "Air", label: "Air" },
    { value: "Farm", label: "Farm" },
    { value: "Factory", label: "Factory" },
    { value: "Bio", label: "Bio" },
    { value: "Life", label: "Life" },
    { value: "Energy", label: "Energy" },
    { value: "Weather", label: "Weather" },
    { value: "City", label: "City" },
    { value: "Traffic", label: "Traffic" },
    { value: "Culture", label: "Culture" },
    { value: "Economy", label: "Economy" }
];


const BasicInfomation = ({ files, rules }) => {
    const { encode, columns } = rules;

    const [domain, setDomain] = useState({
        main_domain: {
            value: ""
        },
        sub_domain: "",
        target_domain: ""
    });
    const [timeIndex, setTimeIndex] = useState({
        format: "",
        value: ""
    });
    const [measurement, setMeasurement] = useState({
        radio: "user",
        value: ""
    });
    const [functionRules, setFunctionRules] = useState(
        [
            {"index": 0, "column": "", "value": "", "func": ""}
        ]
    );

    const [progress, setProgress] = useState(0);
    const [uploadResponse, setUploadResponse] = useState({});

    const handleOnGenerator = (data) => {
        const { uuid_file_name } = data;
        const url = "/api/generate";
        const method= "PUT";
        const headers = {
            "Content-Type": "application/json"
        };
        const params = {
            uuidFileName: uuid_file_name,
            domain: domain.target_domain,
            timeIndex: timeIndex,
            measurement: measurement,
            encode: encode,
            columns: columns
        };

        axios({
            url: url,
            method: method,
            headers: headers,
            data: JSON.stringify(params)
        })
        .then(res => {
            console.log(res);
        })
        .catch(error => {
            console.log(error);
        })

    }

    const handleOnFileUpload = async (files) => {
        try {
            const url = "/api/files";
            const method= "POST";
            const headers = {
                "Content-Type": "multipart/form-data"
            };

            const formData = new FormData();
            formData.append("files", files[0]);

            const response = await axios({
                url: url,
                method: method,
                headers: headers,
                data: formData,
                onUploadProgress: (e) => {
                    const progressRate = (e.loaded / e.total * 100).toFixed(1);
                    setProgress(progressRate);
                },
            });

            const { data, status } = response;
            
            if(status === 200) {
                handleOnGenerator(data);
                setUploadResponse(response);
            }

        } catch (error) {
            setUploadResponse(error);
            console.error(error);
        }
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
                                        value={domain.main_domain}
                                        onChange={
                                            v => setDomain({
                                                ...domain,
                                                main_domain: v,
                                                target_domain: v.value + "__" + domain.sub_domain
                                            })
                                        }
                                        options={defaultMainDomain}
                                        placeholder="option"
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
                                        onChange={
                                            e => setDomain({ 
                                                ...domain,
                                                sub_domain: e.target.value,
                                                target_domain: domain.main_domain.value + "__" + e.target.value
                                            })
                                        }
                                        value={domain.sub_domain || ""}
                                        />
                                </FormGroup>
                            </Col>
                            <Label
                                md="2"
                                style={{
                                    width: "140px"
                                }}
                                >
                                    Target Domain
                            </Label>
                            <Col md="3">
                                <FormGroup>
                                    <Input
                                        readOnly
                                        placeholder=""
                                        type="text"
                                        value={domain.target_domain || ""}
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
                                        value={timeIndex || ""}
                                        onChange={ v => setTimeIndex({ ...timeIndex, data_type: "date", label: v.label, value: v.value }) }
                                        options={
                                            rules.columns !== undefined
                                            ?
                                                columns.map(v => {
                                                    return { ...v, label: v.value };
                                                })
                                            :
                                                []
                                            }
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
                                        onChange={ e => setTimeIndex({ ...timeIndex, format: e.target.value }) }
                                        value={timeIndex.format || ""}
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
                                                    defaultChecked
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
                                            onChange={ e => setMeasurement({ ...measurement, "value": e.target.value }) }
                                            value={measurement.value || ""}
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
                                            value={measurement || ""}
                                            onChange={ e => setMeasurement({ ...measurement, ...e }) }
                                            options={
                                                rules.columns !== undefined
                                                ?
                                                    columns.map(v => {
                                                        return { ...v, label: v.value };
                                                    })
                                                :
                                                    []
                                            }
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
                        {
                            progress !== 0
                            ?
                                <>
                                    <Col md="11">
                                        <ProgressBar
                                            style={{ marginTop: "15px" }}
                                            now={progress}
                                            // label={progress + "%"}
                                            />
                                    </Col>
                                    <Col md="1">
                                        <h6 style={{ marginTop: "12px" }}>{uploadResponse.statusText || progress + "%" }</h6>
                                    </Col>
                                </>
                            :
                                <>
                                    <Col
                                        md="12"
                                        style={{
                                            margin: 0,
                                            textAlign: "right"
                                        }}
                                        >
                                        <Button
                                            color="primary"
                                            style={{ margin: 0 }}
                                            // onClick={ () => files.length !==0 ? handleOnFileUpload(files) : null }
                                            onClick={ () => handleOnFileUpload(files) }
                                            >
                                                commit
                                        </Button>
                                    </Col>
                                </>
                        }
                    </Row>        
                </CardBody>

            </Card>
        </>
    );
}

export default memo(BasicInfomation);