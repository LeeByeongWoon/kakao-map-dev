import React, { memo, useState } from "react";

import CsvPreviewUpload from "./components/CsvPreviewUpload";
import CsvViewer from "./components/CsvViewer";

import BasicInfomation from "./components/BasicInfomation";

import {
    Card,
    CardHeader,
    CardBody,
    CardTitle,
    Row,
    Col
} from "reactstrap";


const defaultCsvData = [
    [
        {"active": false, "type": "field", "index": 0, "label": "Column-1", "value": "Column-1"},
        {"active": false, "type": "field", "index": 1, "label": "Column-2", "value": "Column-2"},
        {"active": false, "type": "field", "index": 2, "label": "Column-3", "value": "Column-3"},
        {"active": false, "type": "field", "index": 3, "label": "Column-4", "value": "Column-4"},
        {"active": false, "type": "field", "index": 4, "label": "Column-5", "value": "Column-5"}
    ],
    [
        "Sample-1.1",
        "Sample-1.2",
        "Sample-1.3",
        "Sample-1.4",
        "Sample-1.5"
    ],
    [
        "Sample-2.1",
        "Sample-2.2",
        "Sample-2.3",
        "Sample-2.4",
        "Sample-2.5"
    ]
];

const CsvImporter = () => {
    const [csvData, setCsvData] = useState(defaultCsvData);
    const [commitData, setCommitData] = useState([]);

    const commitActive = commitData.length === 0;

    const handleOnCommitData = (data) => {
        setCommitData(data);
    }

    const handleOnUpdateCsvData = (data) => {
        setCsvData(data)
    }

    const handleOnUploadCsvData = (data) => {
        const columns = data.slice(0, 1).map(v => v["data"].map(
            (val, idx) => {
                return {"active": false, "type": "field", "index": idx, "label": val, "value": val};
            }
        ));
        const rows = data.slice(1, data.length).map(v => v["data"]);
        const table = columns.concat(rows);

        setCsvData(table);
    }

    return (
        <>
            <div className="content">
                <Row>
                    <Col md="12">
                        <Card>

                            <CsvPreviewUpload
                                defaultCsvData={defaultCsvData}
                                handleOnUploadCsvData={handleOnUploadCsvData}
                                handleOnUpdateCsvData={handleOnUpdateCsvData}
                                />

                            <CsvViewer
                                csvData={csvData}
                                commitActive={commitActive}
                                handleOnCommitData={handleOnCommitData}
                                />

                        </Card>
                    </Col>
                </Row>
                <Row>
                    <Col md="12">
                        <Card>

                            <CardHeader>
                                <CardTitle tag="h4">1. Commit Data</CardTitle>
                            </CardHeader>
                            <CardBody>
                                {
                                    !commitActive
                                    ?
                                    JSON.stringify(commitData.slice(0, 5))
                                    :
                                    ""
                                }
                            </CardBody>

                            <CardHeader>
                                <CardTitle tag="h4">2. Basic Infomation</CardTitle>
                            </CardHeader>
                            <CardBody>
                                <BasicInfomation commitData={commitData}/>
                            </CardBody>

                            {/* <CardFooter
                                style={{
                                    "textAlign": "right",
                                    "paddingTop": 0
                                }}
                                >
                                <Button
                                    color="primary"
                                    onClick={
                                        () => {
                                            if(commitCsvData.length !== 0) {

                                            } else {
                                                handleOnCommitCsvData(csvData)
                                            }
                                        } 
                                    }
                                    >
                                        Submit
                                </Button>
                            </CardFooter> */}

                        </Card>
                    </Col>
                </Row>
            </div>
        </>
    );
}

export default memo(CsvImporter);
