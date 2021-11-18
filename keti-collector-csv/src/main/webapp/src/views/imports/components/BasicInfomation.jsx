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
    { value: "air", label: "air" },
    { value: "farm", label: "farm" },
    { value: "factory", label: "factory" },
    { value: "bio", label: "bio" },
    { value: "life", label: "life" },
    { value: "energy", label: "energy" },
    { value: "weather", label: "weather" },
    { value: "city", label: "city" },
    { value: "traffic", label: "traffic" },
    { value: "culture", label: "culture" },
    { value: "economy", label: "economy" }
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
    const { csv_encode, csv_files } = files;
    const { csv_columns } = rules;

    const [domain, setDomain] = useState({
        main_domain: {},
        sub_domain: "",
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

    // const [progress, setProgress] = useState(0);
    const [responseIsCheck, setResponseIsCheck] = useState(false);
    const [responseValidation, setResponseValidation] = useState({});
    const [responseFiles, setResponseFiles] = useState({
        rf_progress: 0
    });
    const [responseGenerator, setResponseGenerator] = useState({});

    const handleOnGenerator = async (props) => {
        const { uuid_file_name } = props;

        const response_generator = {
            rg_status: null,
            rg_statusText: null,
            rg_data: null
        };

        try {
            const url = "/api/generate/" + measurement.type;
            const method= "POST";
            const headers = {
                "Content-Type": "application/json"
            };

            const params = {
                file_vo: {
                    fl_type: "csv",
                    fl_encode: csv_encode,
                    fl_name: uuid_file_name
                },
                time_series_vo: {
                    ifx_database: {
                        db_main: domain.main_domain.value,
                        db_sub: domain.sub_domain
                    },
                    ifx_measurement: {
                        mt_type: measurement.type,
                        mt_index: measurement.index,
                        mt_value: measurement.value
                    },
                    ifx_columns: csv_columns.map(
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
                },
                meta_vo: {}
            };

            dispatch(inactive());

            const res = await axios({
                url: url,
                method: method,
                headers: headers,
                data: JSON.stringify(params)
            });

            const { status, statusText, data } = res;

            response_generator["rg_status"] = status;
            response_generator["rg_statusText"] = statusText;
            response_generator["rg_data"] = data;
        } catch (error) {
            alert(error);
            console.log(error);
        }
        
        dispatch(active());
        
        return response_generator;
    }

    const handleOnFileUpload = async (props) => {
        const rv_files = props;

        const response_files = {
            rf_status: null,
            rf_statusText: null,
            rf_data: null
        };

        try {
            const url = "/api/generate/file";
            const method= "POST";
            const headers = {
                "Content-Type": "multipart/form-data"
            };

            const formData = new FormData();
            formData.append("files", rv_files[0]);

            const res = await axios({
                url: url,
                method: method,
                headers: headers,
                data: formData,
                onUploadProgress: (e) => {
                    const progressRate = (e.loaded / e.total * 100).toFixed(1);
                    setResponseFiles({
                        ...responseFiles,
                        rf_progress: progressRate
                    });
                },
            });

            const { status, statusText, data } = res;

            response_files["rf_status"] = status;
            response_files["rf_statusText"] = statusText;
            response_files["rf_data"] = data;
        } catch (error) {
            alert(error);
            console.error(error);
        }

        return response_files;
    }

    const handleOnValidation = async () => {
        const response_validation = {
            rv_check: false,
            rv_databases: null,
            rv_measurements: null,
            rv_files: null
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
            return response_validation;
        }

        if(ti_format === undefined || ti_format === null || ti_format === ""
                || ti_value === undefined || ti_value === null || ti_value === "") {
            alert("Time Index Column을 입력해주세요.");
            return response_validation;
        }

        if(mt_type === undefined || mt_type === null || mt_type === ""
                || mt_value === undefined || mt_value === null || mt_value === "") {
            alert("Measurement를 입력해주세요.");
            return response_validation;
        }

        if(csv_files.length === 0) {
            alert("파일을 저장해주세요.");
            return response_validation;
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

            if(databases === undefined && measurement === undefined) {
                return response_validation;
            }

            if(databases.length !== 0) {
                if(!window.confirm(databases + "이 존재합니다. 계속 진행 하시겠습니까?")) {
                    return response_validation;
                }
            }

            if(measurements.length !== 0) {
                if(!window.confirm(measurements + "이 존재합니다. 계속 진행 하시겠습니까?")) {
                    return response_validation;
                }
            }

            response_validation["rv_check"] = true;
            response_validation["rv_databases"] = databases || "";
            response_validation["rv_measurements"] = measurements || "";
            response_validation["rv_files"] = csv_files;
        } catch (error) {
            alert(error);
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
                            {/* <Label
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
                            </Col> */}
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
                                            rules.csv_columns !== undefined
                                            ?
                                            csv_columns.map(v => {
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
                                                rules.csv_columns !== undefined
                                                ?
                                                    csv_columns.map(v => {
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
                                                        rules.csv_columns !== undefined && "value" in timeIndex
                                                        ?
                                                            csv_columns.map(
                                                                v => (
                                                                    timeIndex.value !== v.value 
                                                                    ? { ...v, label: v.value }
                                                                    : { ...v, label: v.value, isDisabled: true, })
                                                            )
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
                </CardBody>

                <CardHeader>
                    <CardTitle tag="h4">3. Commit & Result</CardTitle>
                </CardHeader>
                <CardBody>
                    {
                        responseValidation["rv_check"] !== undefined
                        ?
                            <Row>
                                <Col md="12">
                                    중복체크
                                </Col>
                                <Col md="12">
                                    <h6>{responseValidation["rv_check"] !== true ? "FAIL" : "OK"}</h6>
                                </Col>
                            </Row>
                        :
                            ""
                    }
                    {
                        responseFiles["rf_progress"] !== 0
                        ?
                            <>
                                <Row>
                                    <Col md="12">
                                        파일 업로드
                                    </Col>
                                </Row>
                                <Row>
                                    <Col md="11">
                                        <ProgressBar
                                            style={{ marginTop: "15px" }}
                                            now={responseFiles["rf_progress"]}
                                            // label={responseFiles["rf_progress"] + "%"}
                                            />
                                    </Col>
                                    <Col md="1">
                                        <h6 style={{ marginTop: "12px" }}>
                                            {
                                                responseFiles["rf_status"] !== 200
                                                ?
                                                    responseFiles["rf_progress"] + "%"
                                                :
                                                    "OK"
                                            }
                                        </h6>
                                    </Col>
                                </Row>
                            </>
                        :
                            ""
                    }
                    {
                        responseGenerator["rg_data"] !== undefined
                        ?
                            <Row>
                                <Col md="12">
                                    데이터 저장 결과
                                </Col>
                                <Col md="12">
                                    {JSON.stringify(responseGenerator["rg_data"])}<br /><br />
                                </Col>
                            </Row>
                        :
                            ""
                    }
                    <Row>
                        <Col
                            md="12"
                            style={{
                                margin: 0,
                                textAlign: "right"
                            }}
                            >
                                {
                                    responseGenerator["rg_data"] !== undefined
                                    ?
                                        <Button
                                            color="primary"
                                            style={{ margin: 0 }}
                                            onClick={
                                                () => {
                                                    window.location.reload();
                                                }
                                            }
                                            >
                                                refresh
                                        </Button>
                                    :
                                        <>
                                            {
                                                !responseIsCheck
                                                ?
                                                    <Button
                                                        color="primary"
                                                        style={{ margin: 0 }}
                                                        onClick={
                                                            async () => {
                                                                const response_validation = await handleOnValidation();
                                                                console.log(response_validation);
                                                                const { rv_check, rv_files } = response_validation;

                                                                console.log(response_validation);
            
                                                                setResponseValidation(response_validation);
            
                                                                if(rv_check === true) {
                                                                    setResponseIsCheck(true);

                                                                    const response_files = await handleOnFileUpload(rv_files);
                                                                    const { rf_data } = response_files;
            
                                                                    setResponseFiles(response_files);
            
                                                                    if(rf_data !== null && rf_data !== undefined && rf_data !== "") {
                                                                        const response_generator = await handleOnGenerator(rf_data);
                                                                        setResponseGenerator(response_generator);
                                                                        
                                                                        setResponseIsCheck(false);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        >
                                                            commit
                                                    </Button>
                                                :
                                                    ""
                                            }
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