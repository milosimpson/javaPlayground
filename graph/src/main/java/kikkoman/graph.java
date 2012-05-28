package kikkoman;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.*;

/**
 *
 */
public class graph {

  private static GraphDatabaseService graphDb;
  private static Index<Node> nodeIndex;

  private static String DB_PATH = "graph/db_files";
  private static String PROPERTY_NAME = "propertyName";

  private static void registerShutdownHook()
  {
    // Registers a shutdown hook for the Neo4j and index service instances
    // so that it shuts down nicely when the VM exits (even if you
    // "Ctrl-C" the running example before it's completed)
    Runtime.getRuntime().addShutdownHook( new Thread()
    {
      @Override
      public void run()
      {
        shutdown();
      }
    } );
  }

  private static void shutdown()
  {
    graphDb.shutdown();
  }

  private static enum RelTypes implements RelationshipType
  {
    DEPENDS
  }

  private static Node createAndIndexPropertyName( final String propertyName )
  {
    Node propertyNode = nodeIndex.get( PROPERTY_NAME, propertyName).getSingle();

    if ( propertyNode != null ) {
      System.out.println( "   Found pre-existing node with propertyName " + propertyName );
      return propertyNode;
    }

    Node node = graphDb.createNode();
    node.setProperty( PROPERTY_NAME, propertyName );
    nodeIndex.add( node, PROPERTY_NAME, propertyName );

    return node;
  }

  public static void main( String[] args ) {

    // START SNIPPET: startDb
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
    nodeIndex = graphDb.index().forNodes( "nodes" );
    registerShutdownHook();
    // END SNIPPET: startDb

    List<Set<String>> rulesFrom = new ArrayList<Set<String>>(7);
    List<Set<String>> rulesTo   = new ArrayList<Set<String>>(7);
    List<String> rulesStrings   = new ArrayList<String>(7);

    rulesFrom.add(0, new HashSet<String>() );
    rulesFrom.get(0).addAll( Arrays.<String>asList( "citem.clientid", "citem.type", "citem.externalid" ) );
    rulesTo.add(0, new HashSet<String>() );
    rulesTo.get(0).addAll( Arrays.<String>asList( "citem.id" ) );
    rulesStrings.add(0, "citem.clientid + citem.type + citem.externalid -> citem.id");

    rulesFrom.add(1, new HashSet<String>() );
    rulesFrom.get(1).addAll( Arrays.<String>asList("citem[type=product].parents") );
    rulesTo.add(1, new HashSet<String>() );
    rulesTo.get(1).addAll( Arrays.<String>asList("citem[type=product].ancestorSet") );
    rulesStrings.add(1, "walk(citem[type=product].parents) -> citem[type=product].ancestorSet");

    rulesFrom.add(2, new HashSet<String>() );
    rulesFrom.get(2).addAll( Arrays.<String>asList("citem[type=category].parents") );
    rulesTo.add(2, new HashSet<String>() );
    rulesTo.get(2).addAll( Arrays.<String>asList( "citem[type=category].ancestorSet") );
    rulesStrings.add(2, "walk(citem[type=category].parents) -> citem[type=category].ancestorSet");

    rulesFrom.add(3, new HashSet<String>() );
    rulesFrom.get(3).addAll( Arrays.<String>asList("citem[type=product].ancestorSet") );
    rulesTo.add(3, new HashSet<String>() );
    rulesTo.get(3).addAll( Arrays.<String>asList("citem[type=category].numProducts") );
    rulesStrings.add(3, "count( citem[type=product].ancestorSet ) -> citem[type=category].numProducts");

    rulesFrom.add(4, new HashSet<String>() );
    rulesFrom.get(4).addAll( Arrays.<String>asList("review.syn_about") );
    rulesTo.add(4, new HashSet<String>() );
    rulesTo.get(4).addAll( Arrays.<String>asList("citem[type=product].numReviews") );
    rulesStrings.add(4, "count( review.syn_about ) -> citem[type=product].numReviews");

    rulesFrom.add(5, new HashSet<String>() );
    rulesFrom.get(5).addAll( Arrays.<String>asList("review.syn_about", "review.rating") );
    rulesTo.add(5, new HashSet<String>() );
    rulesTo.get(5).addAll( Arrays.<String>asList("citem[type=product].avgRating") );
    rulesStrings.add(5, "sum( review.rating ) / count( review.syn_about ) -> citem[type=product].avgRating");

    rulesFrom.add(6, new HashSet<String>() );
    rulesFrom.get(6).addAll( Arrays.<String>asList("citem[type=product].numReviews", "citem[type=product].avgRating") );
    rulesTo.add(6, new HashSet<String>());
    rulesTo.get(6).addAll( Arrays.<String>asList("citem[type=category].avgRating") );
    rulesStrings.add(6, "sum( citem[type=product].avgRating * citem[type=product].numReviews ) / sum( citem[type=product].numReviews ) -> citem[type=category].avgRating");

    Set<Node> allNodes = new HashSet<Node>();
    Set<Relationship> allRelationships = new HashSet<Relationship>();

    //// CREATE
    Transaction tx = graphDb.beginTx();
    try
    {
      for( int ruleNumber = 0; ruleNumber < rulesTo.size(); ruleNumber++ ) {

        System.out.println( "Adding nodes and rule #" + ruleNumber + " to the graph: " + rulesStrings.get(ruleNumber) );

        Set<String> from = rulesFrom.get(ruleNumber);

        Set<Node> toNodes   = new HashSet<Node>();
        Set<String> to   = rulesTo.get(ruleNumber);

        for( String toProperty : to ) {
          Node node = createAndIndexPropertyName( toProperty );
          toNodes.add( node );
          allNodes.add( node );
        }

        for( String fromProperty : from ) {
          Node fromNode = createAndIndexPropertyName( fromProperty );

          allNodes.add( fromNode );

          for( Node toNode : toNodes ) {
            Relationship rel = fromNode.createRelationshipTo(toNode, RelTypes.DEPENDS);

            rel.setProperty( "ruleString", rulesStrings.get(ruleNumber) );
            rel.setProperty( "ruleNumber", ruleNumber );

            allRelationships.add(rel);
          }
        }
      }

      // Mutating operations go here
      tx.success();
    }
    finally
    {
      tx.finish();
    }

    //// DELETE
    boolean foundSome = true;
    int level = 0;

    while( foundSome ) {
      foundSome = false;

      // Find levels of calculation, by finding roots, removing them, and then making subsequent passes
      tx = graphDb.beginTx();
      try
      {
        Iterable<Node> iter = GlobalGraphOperations.at( graphDb ).getAllNodes();
        Set<Node> toDelete = new HashSet<Node>();

        for ( Node node : iter ) {

          if ( ! node.hasRelationship( Direction.INCOMING ) && node.hasProperty( PROPERTY_NAME ) ) {

            toDelete.add( node );

            foundSome = true;
          }
        }

        for( Node node : toDelete ) {
          Iterable<Relationship> outgoingRels = node.getRelationships( Direction.OUTGOING );

          System.out.println( "Level = " + level + " property = " + node.getProperty( PROPERTY_NAME ) );

          for( Relationship rel : outgoingRels ) {
            System.out.println( " Clears Rule #" + rel.getProperty( "ruleNumber" ) + " " +  rel.getProperty( "ruleString" ) );

            rel.delete();
          }

          node.delete();
        }

        level++;

      tx.success();
    }
    finally {
      tx.finish();
    }
  }



    //// DELETE
//    tx = graphDb.beginTx();
//    try
//    {
//      for( Relationship rel : allRelationships ) {
//        rel.delete();
//      }
//
//      for( Node node : allNodes ) {
//        node.delete();
//      }
//
//      tx.success();
//    }
//    finally {
//      tx.finish();
//    }

    // graphDb.shutdown();
  }
}
