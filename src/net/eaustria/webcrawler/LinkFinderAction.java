/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eaustria.webcrawler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

/**
 *
 * @author bmayr
 */
// Recursive Action for forkJoinFramework from Java7
public class LinkFinderAction extends RecursiveAction {

    private String url;
    private ILinkHandler c;
    private List<RecursiveAction> urlList = new ArrayList<RecursiveAction>();
    /**
     * Used for statistics
     */
    private static final long t0 = System.nanoTime();

    public LinkFinderAction(String url, ILinkHandler cr) {
        // ToDo: Implement Constructor
        this.url = url;
        this.c = cr;
    }

    @Override
    public void compute() {
        if (c.size() <= 500) {
            NodeFilter hrefNodeFilter = (Node node) -> {
                if (node.getText().contains("a href=\"http")) {
                    return true;
                }
                else {
                    return false;
                }
            };

            //System.out.println(linkHandler.size());
            if (!c.visited(url)) {

                c.addVisited(url);
//            System.out.print("\b\b\b\b\b");
//            System.out.println(linkHandler.size() + "/500");
                System.out.println(url);
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
                        String[] parts = node.getText().split("\"http");
                        String tempURL = "http" + parts[1];

                        tempURL = tempURL.split("\"")[0];

                        //System.out.println(tempURL);
                        if (!tempURL.contains("WARNING") || !tempURL.contains("does not contain")) {
                            urlList.add(new LinkFinderAction(tempURL, c));
                        }

                        //getSimpleLinks(parts[1]);
                    }
                    //System.out.println("####################RUNOUT " + url);
                }
                catch (Exception ex) {
                    //Logger.getLogger(LinkFinder.class.getName()).log(Level.SEVERE, null, ex);
                }

                // ToDo:
                // 1. if crawler has not visited url yet:
                // 2. Create new list of recursiveActions
                // 3. Parse url
                // 4. extract all links from url
                // 5. add new Action for each sublink
                // 6. if size of crawler exceeds 500 -> print elapsed time for statistics
                // -> Do not forget to call ìnvokeAll on the actions!      
            }
            invokeAll(urlList);
        }
    }
}
