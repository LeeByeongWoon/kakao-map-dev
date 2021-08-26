import React, { memo } from "react";

import { CSVReader } from "react-papaparse";

import {
    CardHeader,
    CardTitle,
    CardBody,
    FormGroup,
    Form,
    Row,
    Col
  } from "reactstrap";


const CsvPreviewUpload = (props) => {
    const { defaultCsvData, handleOnUploadCsvData, handleOnUpdateCsvData } = props;

    const handleOnDrop = (data, file) => {
        const { name, size } = file;
        handleOnUploadCsvData(data);
    }
    
    const handleOnError = (err, file, inputElem, reason) => {
        console.log(err)
    }
    
    const handleOnRemoveFile = (data) => {
        handleOnUpdateCsvData(defaultCsvData);
    }

    return (
        <>
            <CardHeader>
                <CardTitle tag="h4">1. CSV UPLOAD</CardTitle>
            </CardHeader>
            <CardBody>
                <Row>
                    <Col md="6">
                        <Form action="#" method="#">
                            <FormGroup>
                                <CSVReader
                                    onDrop={handleOnDrop}
                                    onError={handleOnError}
                                    addRemoveButton
                                    removeButtonColor='#659cef'
                                    onRemoveFile={handleOnRemoveFile}
                                    >
                                        <span>Drop CSV file here or click to upload.</span>
                                </CSVReader>
                            </FormGroup>
                        </Form>
                    </Col>
                </Row>           
            </CardBody>
        </>
    );
}

export default memo(CsvPreviewUpload);
