package pl.orange.queue;

import com.hp.gagawa.java.Document;
import com.hp.gagawa.java.DocumentType;
import com.hp.gagawa.java.elements.*;
import pl.orange.queueComposite.*;
import pl.orange.util.HostListException;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by mateusz on 03.10.15.
 */
public class QueueState {

    Document document;
    private Div container;
    private ArrayList<String> headers;

    private String headerStyle;



    public ArrayList<String> getHeaders() {
        return headers;
    }

    public QueueState() throws HostListException {
        document = new Document(DocumentType.XHTMLTransitional);
        document.head.appendChild( new Title().appendChild( new Text("Complex Example Title") ) );
        document.head.appendChild(new Link().setRel("stylesheet").setHref("../css/bootstrap.css"));
        document.head.appendChild(new Link().setRel("stylesheet").setHref("../css/grid.css"));
        Body body = document.body;
        container = new Div();
        container.setCSSClass("container");
        body.appendChild(container);
        container.appendChild(generatePageHeader());
        container.appendChild(generateQueuesHeaders());
        container.appendChild(generateContent());
    }



    private void prepareHeaderNames() throws HostListException {
        headers = new ArrayList();

        LinkedList<HostListComponent> agregateState =  HostListAgregate.get().getAgregateList();
        for(HostListComponent hlc : agregateState) {
            if(hlc instanceof WifiListComponent){
                for(SimpleHostsList ilc : ((WifiListComponent) hlc).getItems()) {
                    headers.add(hlc.getName() + " ch " + ilc.getName() + " " + ilc.getSize() + "/" + ilc.getMaxSize());
                }
            }
            else {
                headers.add(hlc.getName() + " " + hlc.getSize() + "/" + hlc.getMaxSize());
            }
        }
    }

    private ArrayList<ArrayList<String>> prapareQuqueContent() throws HostListException {
        ArrayList<ArrayList<String>> queueContent = new ArrayList();
        LinkedList<HostListComponent> agregateState =  HostListAgregate.get().getAgregateList();
        for(HostListComponent hlc : agregateState) {
            if(hlc instanceof WifiListComponent){
                for(SimpleHostsList ilc : ((WifiListComponent) hlc).getItems()) {
                    queueContent.add(getHostsListFromSimpleQueue(ilc));
                }
            }
            else {
                queueContent.add(getHostsListFromSimpleQueue((SimpleHostsList) hlc));
            }
        }
        return queueContent;
    }



    private Div generateContent() throws HostListException {
        ArrayList<ArrayList<String>> content = prapareQuqueContent();
        Div contentDiv = new Div();
        contentDiv.setCSSClass("row");
        for(ArrayList<String> contentColumn : content) {
            Div tmpDiv = new Div();
            tmpDiv.setCSSClass(headerStyle);
            tmpDiv.setStyle("background-color: #fff;font-size: 10px;");
            String tmpString = new String();
            for(String contentEntry: contentColumn) {
                tmpString +=contentEntry + "<BR>";
            }
            tmpDiv.appendText(tmpString);
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


    public static void main(String args[]) throws HostListException {
        QueueState qs = new QueueState();
        qs.prepareHeaderNames();
        for(String h: qs.getHeaders()) {
            System.out.println(h);
        }
    }

    private Div generateQueuesHeaders() throws HostListException {
        prepareHeaderNames();
        prepareHeaderLength();
        Div row = new Div();
        row.setCSSClass("row");
        for(String headerName: getHeaders()) {
            Div tmp = new Div();
            tmp.appendChild(new Text(headerName));
            tmp.setCSSClass(headerStyle);
            row.appendChild(tmp);
        }
        return row;

    }

    private void prepareHeaderLength() {
        int headerSize = 12 / getHeaders().size();
        headerStyle = "col-md-" + Integer.toString(headerSize);
    }


    private Div generatePageHeader() {
        Div pageHeader = new Div();
        pageHeader.setCSSClass("page-header");
        H1 header = new H1();
        header.appendChild(new Text("Statystyki kolejki"));
        pageHeader.appendChild(header);
        return pageHeader;

    }


    public String getQueueState() {
        return document.write();
    }
}
