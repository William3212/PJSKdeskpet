package com.group_finity.mascot.virtual;

import java.awt.image.BufferedImage;

import com.group_finity.mascot.NativeFactory;
import com.group_finity.mascot.environment.Environment;
import com.group_finity.mascot.image.NativeImage;
import com.group_finity.mascot.image.TranslucentWindow;

/**
 * Virtual desktop environment by Kilkakon
 * kilkakon.com
 */
public class NativeFactoryImpl extends NativeFactory
{
    private VirtualEnvironment environment = new VirtualEnvironment( );
    
    @Override
    public Environment getEnvironment( )
    {
        return this.environment;
    }
    
    @Override
    public NativeImage newNativeImage( final BufferedImage src )
    {
        return new VirtualNativeImage( src );
    }

    @Override
    public TranslucentWindow newTransparentWindow( )
    {
        VirtualTranslucentPanel panel = new VirtualTranslucentPanel( );
        environment.addShimeji( panel );
        return panel;
    }
}
