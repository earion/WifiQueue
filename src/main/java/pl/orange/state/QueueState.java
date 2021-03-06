package pl.orange.state;

import com.hp.gagawa.java.Document;
import com.hp.gagawa.java.DocumentType;
import com.hp.gagawa.java.elements.*;
import pl.orange.queueComposite.*;
import pl.orange.util.HostListException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by mateusz on 03.10.15.
 */
class QueueState {

    private Document document;
    private Div container;
    private ArrayList<HeaderInformation> headersData;
    private String headerStyle;
    private Div errorDiv;

    QueueState() throws HostListException {
        document = new Document(DocumentType.XHTMLTransitional);
        document.head.appendChild(new Title().appendChild(new Text("Statystyki kolejek")));
        document.head.appendChild(new Text("<Meta charset='UTF-8'>"));
        document.head.appendChild(new Meta("30").setHttpEquiv("refresh"));
        document.head.appendChild(new Link().setRel("stylesheet").setHref("../css/bootstrap.css"));
        document.head.appendChild(new Link().setRel("stylesheet").setHref("../css/grid.css"));
        Body body = document.body;
        container = new Div();
        container.setCSSClass("container");
        body.appendChild(container);
        container.appendChild(generatePageHeader());
        container.appendChild(genereateErrorDiv());
        container.appendChild(generateQueuesHeaders());
        container.appendChild(generateContent());
    }

    private String getHostQueueHeaderName() {
        String content =  "Kolejka Hostów ";
        try {
            content +=  InetAddress.getLocalHost().getHostName();
            BufferedReader is = new BufferedReader(( new InputStreamReader(getClass().getClassLoader().getResourceAsStream("version"))));
            content += " wersja "  +   is.readLine();
        } catch ( IOException e) {
            e.printStackTrace();
        }
        return content;
    }



    private Div genereateErrorDiv() {
        errorDiv = new Div();
        errorDiv.setStyle("background-color: #fff;color:red;height:20px");
        return errorDiv;
    }

    private static Select buildDropDown(String max) {
        Select select = new Select();
        select.setCSSClass("form-control");
        select.setName("size");
        select.setSize("1");
        for (int i=10;i<210;i+=10) {
            String value = Integer.toString(i);
            String text = Integer.toString(i);
            Option opt = new Option();
            if(text.equals(max)) opt.setSelected(max);
            opt.setValue(value);
            opt.appendChild(new Text(text));
            select.appendChild(opt);
        }
        select.setAttribute("onchange","this.form.submit()");
        return select;
    }

    private void prepareHeaderNames() throws HostListException {
        headersData = new ArrayList<>();
        LinkedList<HostListComponent> agregateState =  HostListAgregate.get().getAgregateList();
        for(HostListComponent hlc : agregateState) {
            if(hlc instanceof WifiListComponent){
                for(SimpleHostsList ilc : ((WifiListComponent) hlc).getItems()) {
                    headersData.add(new HeaderInformation(hlc.getName(),ilc.getName(),ilc.getSize(),ilc.getMaxSize()));
                }
            } else if(hlc instanceof OntListComponent) {
                headersData.add(new HeaderInformation(hlc.getName(),
                        "SLOT: " + Integer.toString(((OntListComponent) hlc).getOltId()),
                        hlc.getSize(),
                        hlc.getMaxSize()));
            }
            else {
                headersData.add(new HeaderInformation(hlc.getName(),hlc.getSize(),hlc.getMaxSize()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<ArrayList<String>> prapareQuqueContent() throws HostListException {
        ArrayList queueContent = new ArrayList();
        LinkedList<HostListComponent> agregateState =  HostListAgregate.get().getAgregateList();
        for(HostListComponent hlc : agregateState) {
            if(hlc instanceof WifiListComponent){
                for(SimpleHostsList ilc : ((WifiListComponent) hlc).getItems()) {
                    queueContent.add(getHostsListFromSimpleQueue(ilc));
                }
            } else if(hlc instanceof OntListComponent) {
                queueContent.add(getHostsListFromOntList((OntListComponent) hlc));
            }
            else {
                queueContent.add(getHostsListFromSimpleQueue((SimpleHostsList) hlc));
            }
        }
        return queueContent;
    }

    private ArrayList<String> getHostsListFromOntList(OntListComponent olc) {
        ArrayList<String> list = new ArrayList<>();
        for(HostListComponent h: olc.getItems()) {
            list.add(h.getName() + " " + ((Host) h).getCreationDate());
        }
        return list;

    }

    private Div generateContent() throws HostListException {
        ArrayList<ArrayList<String>> content = prapareQuqueContent();
        Div contentDiv = new Div();
        contentDiv.setCSSClass("row");
        for(ArrayList<String> contentColumn : content) {
            Div tmpDiv = new Div();
            tmpDiv.setCSSClass(headerStyle);
            tmpDiv.setStyle("background-color: #fff;font-size: 10px;height:400px");
            StringBuilder tmpString = new StringBuilder();
            for(String contentEntry: contentColumn) {
                tmpString.append(contentEntry).append("<BR>");
            }
            tmpDiv.appendText(tmpString.toString());
            contentDiv.appendChild(tmpDiv);
        }
        return contentDiv;
    }

    private ArrayList<String> getHostsListFromSimpleQueue(SimpleHostsList l) {
        ArrayList<String> list = new ArrayList<>();
        for(HostListComponent h: l.getItems()) {
            list.add(h.getName() + " " + ((Host) h).getCreationDate());
        }
        return list;
    }

    private Div generateQueuesHeaders() throws HostListException {
        prepareHeaderNames();
        prepareHeaderLength();
        Div row = new Div();
        row.setCSSClass("row");
        for(HeaderInformation headerData: headersData) {
            Div tmpDiv = generateDivForOneHeader(headerData);
            row.appendChild(tmpDiv);
        }
        return row;

    }

    private Div generateDivForOneHeader(HeaderInformation headerData) {
        Div tmpDiv = new Div();
        Div columnHeaderDir = new Div();
        tmpDiv.setCSSClass(headerStyle);
        tmpDiv.setStyle("height :100px");
        columnHeaderDir.appendChild(new Text(headerData.getHeaderName()));
        columnHeaderDir.setStyle("height: 60px");
        tmpDiv.appendChild(columnHeaderDir);
        if(!headerData.getHeaderName().toLowerCase().contains("ont")) {
            Form tmpForm = new Form("modify");
            Input hiddenInput = new Input();
            hiddenInput.setType("Hidden");
            hiddenInput.setName("name");
            hiddenInput.setValue(headerData.getName());
            tmpForm.appendChild(hiddenInput);
            tmpForm.appendChild(buildDropDown(headerData.getMaxValueAsString()));
            tmpDiv.appendChild(tmpForm);
        }
        return tmpDiv;
    }

    private void prepareHeaderLength() {
        int headerSize = 12 / headersData.size();
        headerStyle = "col-md-" + Integer.toString(headerSize);
    }

    private Div generatePageHeader() {
        Div pageHeader = new Div();
        pageHeader.setCSSClass("page-header");
        H1 header = new H1();
        header.appendChild(new Text(getHostQueueHeaderName()));
        pageHeader.appendChild(header);
        return pageHeader;

    }

    QueueState setError(String error) {
        errorDiv.appendText(error);
        return this;
    }


    String getQueueState() {
        return document.write();
    }

    private class HeaderInformation {
        String name;
        String subname;
        int curentValue;
        int maxValue;

        String getHeaderName() {
            return name + " " + subname + " " + Integer.toString(curentValue) + "/" + Integer.toString(maxValue);
        }

        HeaderInformation(String name, String subname, int curentValue, int maxValue) {
            this.name = name;
            this.subname = subname;
            this.curentValue = curentValue;
            this.maxValue = maxValue;
        }

        HeaderInformation(String name, int curentValue, int maxValue) {
            this.name = name;
            this.curentValue = curentValue;
            this.maxValue = maxValue;
            this.subname = "";
        }

        public String getName() {
            return name;
        }

        String getMaxValueAsString() {
            return Integer.toString(maxValue);
        }
    }
}
