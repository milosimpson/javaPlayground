/*
 * Copyright (C) 2009-2011 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kikkoman.parboiled.path;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * STEP 1 Basic literal Only, no array Parser
 * "tuna.marlin.pants"  Should return an OutputPath, which is basically
 *  an Array of LiteralPathElements.
 *
 */
@SuppressWarnings({"InfiniteRecursion"})
@BuildParseTree
public class SingleOutputPathParser extends BaseParser<SingleOutputPathParser.OutputPath> {

    public static void main(String[] args) {

        SingleOutputPathParser myParser = Parboiled.createParser( SingleOutputPathParser.class );
        ParsingResult<?> result = new ReportingParseRunner( myParser.TheMainRule() ).run(
            "tuna.marlin.pants" );

        OutputPath theThingIWant = ( (OutputPath) result.resultValue );

        if ( !result.parseErrors.isEmpty() ) {
            System.out.println( ErrorUtils.printParseError( result.parseErrors.get( 0 ) ) );
        }
        else {
            System.out.println( theThingIWant.getCanonicalForm() );
        }

    }

    OutputPath myOutputPath = new OutputPath();

    public Rule Dot() {
        return Ch( '.' );
    }

    public Rule Meat() {
        return Sequence(
                // Catch all chars other than "."
                OneOrMore( NoneOf( "." ) ),
                // If that worked / matched, then grb that text and build up the "myOutputPath"
                // "push" is special, normally Sequence has a series of rules, but you can
                //  do this special push thing to capture content.
                push( myOutputPath.addElement( new LiteralPathElement( match() ) ) ) );
    }


    public Rule TheMainRule() {
        return Sequence(
                // at least find a single level output path, aka "tuna"
                Meat(),
                // Then look for zeroOrMore repeated pairs of "." and meat
                ZeroOrMore( Dot(), Meat() ) );
    }

    public static class LiteralPathElement {
        public final String rawString;

        public LiteralPathElement( String rawString ) {
            this.rawString = rawString;
        }
    }

    public static class OutputPath {
        public final List<LiteralPathElement> pathElements = new ArrayList<>();

        public OutputPath() {
        }

        public OutputPath addElement( LiteralPathElement lpe ) {
            pathElements.add( lpe );
            return this;
        }

        public String getCanonicalForm() {
            boolean seenOne = false;
            StringBuilder sb = new StringBuilder();
            for ( LiteralPathElement lpe : pathElements ) {
                if ( seenOne ) {
                    sb.append( "." );
                }
                seenOne = true;
                sb.append( lpe.rawString );
            }
            return sb.toString();
        }
    }
}
