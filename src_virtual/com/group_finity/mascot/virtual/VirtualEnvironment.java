package com.group_finity.mascot.virtual;

import com.group_finity.mascot.environment.Area;
import com.group_finity.mascot.environment.Environment;
import com.group_finity.mascot.Main;
//import com.group_finity.mascot.virtual.jna.TranslucentFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Virtual desktop environment by Kilkakon
 * kilkakon.com
 */
class VirtualEnvironment extends Environment
{
    private final JFrame display = new JFrame( );
    
    private final Area activeIE = new Area( );

    @Override
    public Area getWorkArea( )
    {
        return getScreen( );
    }

    @Override
    public Area getActiveIE( )
    {
        return activeIE;
    }

    @Override
    public String getActiveIETitle( )
    {
        return null;
    }

    @Override
    public void moveActiveIE( final Point point )
    {
    }

    @Override
    public void restoreIE( ) 
    {
    }

    @Override
    public void refreshCache( )
    {
        // I feel so refreshed
        
        // good for you buddy
    }
    
    @Override
    public void init( )
    {
        if( !display.isVisible( ) )
        {
            display.addWindowListener( new WindowListener( )
            {
                @Override
                public void windowOpened( WindowEvent e ) { }

                @Override
                public void windowClosing( WindowEvent e )
                {
                    Main.getInstance( ).exit( );
                }

                @Override
                public void windowClosed( WindowEvent e ) { }

                @Override
                public void windowIconified( WindowEvent e ) { }

                @Override
                public void windowDeiconified( WindowEvent e ) { }

                @Override
                public void windowActivated( WindowEvent e ) { }

                @Override
                public void windowDeactivated( WindowEvent e ) { }
            } );
            display.setAutoRequestFocus( false );
            display.setTitle( Main.getInstance( ).getLanguageBundle( ).getString( "ShimejiEE" ) );
            String[ ] windowArray = Main.getInstance( ).getProperties( ).getProperty( "WindowSize", "600x500" ).split( "x" );
            BufferedImage image = null;
            try
            {
                if( !Main.getInstance( ).getProperties( ).getProperty( "BackgroundImage", "" ).isEmpty( ) )
                    image = ImageIO.read( new File( Main.getInstance( ).getProperties( ).getProperty( "BackgroundImage", "" ) ) );
            }
            catch( Exception ex )
            {
            }
            display.setContentPane( new VirtualContentPanel( new Dimension( Integer.parseInt( windowArray[ 0 ] ), Integer.parseInt( windowArray[ 1 ] ) ),
                                                             Color.decode( Main.getInstance( ).getProperties( ).getProperty( "Background", "#00FF00" ) ),
                                                             image,
                                                             Main.getInstance( ).getProperties( ).getProperty( "BackgroundMode", "centre" ) ) );
            display.setBackground( display.getContentPane( ).getBackground( ) );

            BufferedImage icon = null;
            try
            {
                icon = ImageIO.read( Main.class.getResource( "/icon.png" ) );
            }
            catch( Exception ex )
            {
                // not bothering reporting errors with loading the tray icon as it would have already been reported to the user by now
            }
            finally
            {
                if( icon == null )
                    icon = new BufferedImage( 16, 16, BufferedImage.TYPE_INT_RGB );
            }
            display.setIconImage( icon );

            SwingUtilities.invokeLater( new Runnable( )
            {
                @Override
                public void run( )
                {
                    display.pack( );
                    display.setVisible( true );
                    display.toFront( );
                }
            } );
        
            activeIE.set( new Rectangle( -500, -500, 0, 0 ) );
            screenRect.setBounds( display.getContentPane( ).getBounds( ) );
        }
        
        tick( );
    }

    @Override
    public void tick( )
    {
        if( display.isVisible( ) )
        {
            screenRect.setBounds( display.getContentPane( ).getBounds( ) );
            screen.set( screenRect );
        }
        
        java.awt.PointerInfo info = MouseInfo.getPointerInfo( );
        Point point = new Point( 0, 0 );
        if( info != null && display.isVisible( ) )
        {
            point = info.getLocation( );
            SwingUtilities.convertPointFromScreen( point, display.getContentPane( ) );
        }
        cursor.set( point );
    }

    @Override
    public void dispose( )
    {
        if( display != null )
            display.dispose( );
    }
    
    public void addShimeji( final VirtualTranslucentPanel shimeji )
    {
        SwingUtilities.invokeLater( new Runnable( )
        {
            @Override
            public void run( )
            {
                if( display.getContentPane( ).getSize( ).width > 0 && display.getContentPane( ).getSize( ).height > 0 )
                {
                    display.setPreferredSize( display.getSize( ) );
                    display.getRootPane( ).setPreferredSize( display.getRootPane( ).getSize( ) );
                    display.getContentPane( ).setPreferredSize( display.getContentPane( ).getSize( ) );
                }
                shimeji.setOpaque( false );
                display.getContentPane( ).add( shimeji );
            }
        } );
    }
}
