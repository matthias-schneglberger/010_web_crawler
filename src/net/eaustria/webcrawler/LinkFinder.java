/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eaustria.webcrawler;

/**
 *
 * @author bmayr
 */
import java.net.URL;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;//
import org.htmlparser.util.SimpleNodeIterator;

public class LinkFinder implements Runnable {

    private String url;
    private ILinkHandler linkHandler;
    /**
     * Used fot statistics
     */
    private static final long t0 = System.nanoTime();
    

    

    public LinkFinder(String url, ILinkHandler handler) {
        //ToDo: Implement Constructor
        this.url = url;
        this.linkHandler = handler;
    }

    @Override
    public void run() {
        getSimpleLinks(url);
    }

    private void getSimpleLinks(String url) {
        NodeFilter hrefNodeFilter = (Node node) -> {
            if (node.getText().contains("a href=\"http:")) {
                return true;
            } else {
                return false;
            }
        };
        
        
        
        if(!linkHandler.visited(url)){
            linkHandler.addVisited(url);
            URL newURL;
           
            
            try {
                newURL = new URL(url);
                Parser parser = new Parser(newURL.openConnection());
                NodeList nodes = parser.extractAllNodesThatMatch(hrefNodeFilter);
                
                
                SimpleNodeIterator iterator = nodes.elements();
                //System.out.println(nodes.size());
                while (iterator.hasMoreNodes()) {
                    Node node = iterator.nextNode();
                    //System.out.println(node.getText());
                    String[] parts = node.getText().split("\"");
                    System.out.println(parts[1]);
                    linkHandler.queueLink((parts[1]));
                    getSimpleLinks(parts[1]);
                }
                System.exit(0);
                
            }
            catch (Exception ex) {
                Logger.getLogger(LinkFinder.class.getName()).log(Level.SEVERE, null, ex);
            }
//            
            
            
            
            linkHandler.addVisited(url);
            if(linkHandler.size() >= 500){
                System.out.println(System.nanoTime()-t0);
            }
        }
        
        
        
        // ToDo: Implement
        // 1. if url not already visited, visit url with linkHandler
        // 2. get url and Parse Website
        // 3. extract all URLs and add url to list of urls which should be visited
        //    only if link is not empty and url has not been visited before
        // 4. If size of link handler equals 500 -> print time elapsed for statistics               
        
    }
}

