package org.kitona.zus.business.demo;

import org.kitona.zus.business.entity.engine.FgaModelImporter;

import java.net.URI;
import java.net.URISyntaxException;

/*
 * Author: 登林
 * Email: wangli.liu@kitona.org
 * Date: 2025/11/12 23:24
 * Version: V1.0
 * Description: Xxxx
 */public class FgaModelImporterDemo {

    public static void main(String[] args) throws URISyntaxException {
        System.out.println(FgaModelImporter.importFromFga(new URI("https://raw.githubusercontent.com/openfga/sample-stores/refs/heads/main/stores/iot/model.fga")));
    }
}
