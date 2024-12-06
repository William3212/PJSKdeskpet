package com.group_finity.mascot.virtual;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

//import javax.swing.Icon;
//import javax.swing.ImageIcon;

import com.group_finity.mascot.image.NativeImage;

/**
 * Virtual desktop environment by Kilkakon
 * kilkakon.com
 */
class VirtualNativeImage implements NativeImage
{
    /**
     * Java Image object.
     */
    private final BufferedImage managedImage;

    //private final Icon icon;

    public VirtualNativeImage( final BufferedImage image )
    {
        managedImage = image;
        //icon = new ImageIcon( image );
    }

//    @Override
//    protected void finalize( ) throws Throwable
//    {
//            super.finalize();
//    }

    public void flush( )
    {
        managedImage.flush( );
    }

    public Graphics getGraphics( )
    {
        return managedImage.createGraphics( );
    }

    public int getHeight( )
    {
        return managedImage.getHeight( );
    }

    public int getWidth( )
    {
        return managedImage.getWidth( );
    }

    public int getHeight( final ImageObserver observer )
    {
        return managedImage.getHeight( observer );
    }

    public Object getProperty( final String name, final ImageObserver observer )
    {
        return managedImage.getProperty( name, observer );
    }

    public ImageProducer getSource( )
    {
        return managedImage.getSource( );
    }

    public int getWidth( final ImageObserver observer )
    {
        return managedImage.getWidth( observer );
    }

    BufferedImage getManagedImage( )
    {
        return managedImage;
    }

//    public Icon getIcon( )
//    {
//        return icon;
//    }
}
