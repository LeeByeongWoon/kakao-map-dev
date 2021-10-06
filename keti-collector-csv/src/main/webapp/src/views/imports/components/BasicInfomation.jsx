import React, { memo, useState } from "react";
import { useDispatch } from "react-redux";

import { active, inactive } from "modules/store/indicator";

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

const sign = {
    char: [
        { value: "!=", label: "!=" },
        { value: "==", label: "==" }
    ],
    float: [
        { value: "!=", label: "!=" },
        { value: "==", label: "==" },
        { value: ">", label: ">" },
        { value: "<", label: "<" }
    ]
};


const BasicInfomation = ({ files, rules }) => {
    const dispatch = useDispatch();
    const { encode, columns } = rules;

    const [domain, setDomain] = useState({
        main_domain: {},
        sub_domain: "test_01",
        target_domain: ""
    });
    const [timeIndex, setTimeIndex] = useState({
        format: "yyyy/MM/dd-HH:mm:ss"
    });
    const [measurement, setMeasurement] = useState({
        type: "input"
    });
    const [compareRules, setCompareRules] = useState([
        {}
    ]);

    const [progress, setProgress] = useState(0);
    const [uploadResponse, setUploadResponse] = useState({});

    const handleOnGenerator = ({ uuid_file_name }) => {
        const { type } = measurement;

        const url = "/api/generate/" + type;
        const method= "POST";
        const headers = {
            "Content-Type": "application/json"
        };

        const params = {
            file: {
                fl_type: "csv",
                fl_encode: encode,
                fl_name: uuid_file_name
            },
            influxdb: {
                ifx_database: {
                    db_main: domain.main_domain.value,
                    db_sub: domain.sub_domain
                },
                ifx_measurement: {
                    mt_type: measurement.type,
                    mt_index: measurement.index,
                    mt_value: measurement.value
                },
                ifx_columns: columns.map(
                    v => {
                        const compareData = compareRules.filter(val => v.value === (val.compareColumn !== undefined ? val.compareColumn.value : []));
                        const column = 
                            v.value !== timeIndex.value 
                            ?
                                {
                                    data_index: v.index,
                                    data_set: v.data_set,
                                    data_type: v.data_type,
                                    data_format: v.data_format,
                                    data_value: v.value,
                                    data_func: compareData.map(
                                        val => ({
                                            compare_sign: val.compareSign.value,
                                            compare_value: val.compareValue
                                        })
                                    )
                                }
                            :
                                {
                                    data_index: v.index,
                                    data_set: "time",
                                    data_type: "Date",
                                    data_format: timeIndex.format,
                                    data_value: v.value,
                                    data_func: []
                                }
                        
                        return column;
                    }
                )
            }
        };

        dispatch(inactive());

        axios({
            url: url,
            method: method,
            headers: headers,
            data: JSON.stringify(params)
        })
        .then(res => {
            console.log(res);
            dispatch(active());
        })
        .catch(error => {
            console.log(error.response);
            dispatch(active());
        })
    }

    const handleOnFileUpload = async ({ vld_files }) => {
        const response_files = {
            status: null,
            statusText: null,
            data: null
        };

        try {
            const url = "/api/files";
            const method= "POST";
            const headers = {
                "Content-Type": "multipart/form-data"
            };

            const formData = new FormData();
            formData.append("files", vld_files[0]);

            const res = await axios({
                url: url,
                method: method,
                headers: headers,
                data: formData,
                onUploadProgress: (e) => {
                    const progressRate = (e.loaded / e.total * 100).toFixed(1);
                    setProgress(progressRate);
                },
            });

            const { status, statusText, data } = res;
            
            if(res.status === 200) {
                response_files["status"] = status;
                response_files["statusText"] = statusText;
                response_files["data"] = data;
                // handleOnGenerator(res.data);
            }

        } catch (error) {
            const { status, statusText, data } = error;
            response_files["status"] = status;
            response_files["statusText"] = statusText;
            response_files["data"] = data;
            console.error(error);
        }

        setUploadResponse(response_files);

        return response_files;
    }

    const handleOnValidation = async () => {
        const response_validation = {
            vld_databases: null,
            vld_measurements: null,
            vld_files: null
        };

        const { main_domain, sub_domain } = domain;
        const md_value = main_domain.value;

        const ti_format = timeIndex.format;
        const ti_value = timeIndex.value;

        const mt_type = measurement.type;
        const mt_value = measurement.value;

        if(md_value === undefined || md_value === null || md_value === ""
                || sub_domain === undefined || sub_domain === null || sub_domain === "") {
            alert("Main domain, Sub domain을 입력해주세요.");
            return false;
        }

        if(ti_format === undefined || ti_format === null || ti_format === ""
                || ti_value === undefined || ti_value === null || ti_value === "") {
            alert("Time Index Column을 입력해주세요.");
            return false;
        }

        if(mt_type === undefined || mt_type === null || mt_type === ""
                || mt_value === undefined || mt_value === null || mt_value === "") {
            alert("Measurement를 입력해주세요.");
            return false;
        }

        if(files.length === 0) {
            alert("파일을 저장해주세요.");
            return false;
        }

        try {
            const url =
                measurement.type !== "input"
                    ?
                    "/api/validation/columns?main_domain=" + domain.main_domain.value + "&sub_domain=" + domain.sub_domain + "&measurement="
                    :
                    "/api/validation/input?main_domain=" + domain.main_domain.value + "&sub_domain=" + domain.sub_domain + "&measurement=" + measurement.value;
            const method= "GET";
            const headers = {
                "Content-Type": "application/json"
            };

            const res = await axios({
                url: url,
                method: method,
                headers: headers,
            });

            const { databases, measurements } = res.data;

            if(databases.length !== 0) {
                if(!window.confirm(databases + "이 존재합니다. 계속 진행 하시겠습니까?")) {
                    return;
                }
            }

            if(measurements.length !== 0) {
                if(!window.confirm(measurements + "이 존재합니다. 계속 진행 하시겠습니까?")) {
                    return;
                }
            }

            response_validation["vld_databases"] = databases || "";
            response_validation["vld_measurements"] = measurements || "";
            response_validation["vld_files"] = files;

        } catch (error) {
            console.error(error);
        }

        return response_validation;
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
                                        value={
                                            domain.main_domain.value !== undefined
                                            ?
                                                domain.main_domain || ""
                                            :
                                                ""
                                        }
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
                                        value={
                                            timeIndex.value !== undefined
                                            ?
                                                timeIndex || ""
                                            :
                                                ""
                                        }
                                        onChange={ v => setTimeIndex({ ...timeIndex, data_type: "Char", label: v.label, value: v.value }) }
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
                            <Col md="3">
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
                                                    onClick={ e => setMeasurement({...measurement, "type": "input"}) }
                                                    />
                                                        generated-by-input<span className="form-check-sign" />
                                            </Label>
                                    </div>
                                </FormGroup>
                            </Col>
                            <Col md="3">
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
                                                    onClick={ e => setMeasurement({...measurement, "type": "columns"}) }
                                                    />
                                                        generated-by-columns <span className="form-check-sign" />
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
                                        display: measurement.type !== "input" ? "none" : "block"
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
                                        display: measurement.type !== "columns" ? "none" : "block"
                                    }}
                                    >
                                        <Select
                                            className="react-select primary"
                                            classNamePrefix="react-select"
                                            name="measurement"
                                            value={
                                                measurement.value !== undefined
                                                ?
                                                    measurement || ""
                                                :
                                                    ""
                                            }
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
                        compareRules.map((v, i) => {
                            return (
                                <Row key={"compareRules" + i + "_" + v}>
                                    <Col md="3">
                                        <Form action="#" method="#">
                                            <label>Column_{i+1}</label>
                                            <FormGroup>
                                                <Select
                                                    className="react-select primary"
                                                    classNamePrefix="react-select"
                                                    name={"compareColumn_" + i}
                                                    value={
                                                        compareRules[i]["compareColumn"] !== undefined
                                                        ?
                                                            compareRules[i]["compareColumn"] || ""
                                                        :
                                                        ""
                                                    }
                                                    onChange={
                                                        e => {
                                                            const temp = [
                                                                ...compareRules
                                                            ];
                                                            temp[i] = {
                                                                // ...compareRules[i],
                                                                compareColumn: e
                                                            };

                                                            setCompareRules(temp);
                                                        }
                                                    }
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
                                        </Form>
                                    </Col>
                                    <Col md="3">
                                        <Form action="#" method="#">
                                            <label>Function_{i+1}</label>
                                            <FormGroup>
                                                <Select
                                                    className="react-select primary"
                                                    classNamePrefix="react-select"
                                                    name="compareSign"
                                                    value={
                                                        compareRules[i]["compareSign"] !== undefined
                                                        ?
                                                            compareRules[i]["compareSign"] || ""
                                                        :
                                                        ""
                                                    }
                                                    onChange={
                                                        e => {
                                                            const temp = [
                                                                ...compareRules
                                                            ];
                                                            temp[i] = {
                                                                ...compareRules[i],
                                                                compareSign: e
                                                            };

                                                            setCompareRules(temp);
                                                        }
                                                    }
                                                    options={
                                                        compareRules[i]["compareColumn"] !== undefined
                                                        ?
                                                            compareRules[i]["compareColumn"].data_type !== "Float"
                                                            ?
                                                                sign.char
                                                            :
                                                                sign.float
                                                        :
                                                            []
                                                    }
                                                    placeholder="option"
                                                    />
                                            </FormGroup>
                                        </Form>
                                    </Col>
                                    <Col md="3">
                                        <Form action="#" method="#">
                                            <label>Value_{i+1}</label>
                                            <FormGroup>
                                                <Input
                                                    type="text"
                                                    onChange={
                                                        (e) => {
                                                            const temp = [
                                                                ...compareRules
                                                            ];
                                                            temp[i] = {
                                                                ...compareRules[i],
                                                                compareValue: e.target.value
                                                            };

                                                            setCompareRules(temp);
                                                        }
                                                    }
                                                    />
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
                                            const temp = [...compareRules];
                                            temp.push({});

                                            setCompareRules(temp);
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
                                            const temp = [...compareRules];
                                            temp.splice(temp.length-1, 1);

                                            setCompareRules(temp);
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
                                            onClick={
                                                async () => {
                                                    const validation = await handleOnValidation();
                                                    const { data } = await handleOnFileUpload(validation);
                                                    handleOnGenerator(data);
                                                }
                                            }
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