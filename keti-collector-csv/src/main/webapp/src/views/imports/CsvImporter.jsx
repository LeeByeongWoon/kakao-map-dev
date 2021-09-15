import React, { memo, useState } from "react";


import CsvPreviewer from "./components/CsvPreviewer";
import BasicInfomation from "./components/BasicInfomation";

import {
    Row,
    Col
} from "reactstrap";


const CsvImporter = () => {
    const [files, setFiles] = useState([]);
    const [rules, setRules] = useState({});


    const handleOnSetRules = (rules) => {
        setRules(rules);
    }

    const handleOnSetFiles = (files) => {
        setFiles(files);
    }

    return (
        <>
            <div className="content">
                <Row>
                    <Col md="12">
                        <CsvPreviewer
                            handleOnSetFiles={handleOnSetFiles}
                            handleOnSetRules={handleOnSetRules}
                            />
                    </Col>
                </Row>
                <Row>
                    <Col md="12">
                        <BasicInfomation 
                            files={files}
                            rules={rules}
                            />
                    </Col>
                </Row>
            </div>
        </>
    );
}

export default memo(CsvImporter);