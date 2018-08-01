package xtend519;

import java.util.List;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

public class MyTestEngine implements TestEngine {

    @Override
    public String getId( ) {
        return "MyTestEngine";
    }

    @Override
    public TestDescriptor discover( EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId ) {
        final EngineDescriptor engineDescriptor = new EngineDescriptor( uniqueId, "MyTestEngine" );
        final List<ClassSelector> classSelectors = discoveryRequest.getSelectorsByType( ClassSelector.class );
        if ( classSelectors.isEmpty( ) ) {
            return engineDescriptor;
        }
        final ClassSelector classSelector = classSelectors.get( 0 );
        final Class<?> javaClass = classSelector.getJavaClass( );

        engineDescriptor.addChild( new AbstractTestDescriptor( uniqueId.append( "test", javaClass.getName( ) ), javaClass.getSimpleName( ), ClassSource.from( javaClass ) ) {

            @Override
            public Type getType( ) {
                return Type.TEST;
            }
        } );
        return engineDescriptor;
    }

    @Override
    public void execute( ExecutionRequest request ) {
        TestDescriptor engineDescriptor = request.getRootTestDescriptor( );
        EngineExecutionListener listener = request.getEngineExecutionListener( );

        listener.executionStarted( engineDescriptor );
        for ( TestDescriptor testDescriptor : engineDescriptor.getChildren( ) ) {
            listener.executionStarted( testDescriptor );
            listener.executionFinished( testDescriptor, TestExecutionResult.successful( ) );
        }
        listener.executionFinished( engineDescriptor, TestExecutionResult.successful( ) );
    }

}
