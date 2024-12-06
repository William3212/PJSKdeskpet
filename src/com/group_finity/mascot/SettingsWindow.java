/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.group_finity.mascot;

import java.awt.Desktop;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;
import com.nilo.plaf.nimrod.NimRODFontDialog;

/**
 * @author Kilkakon
 */
public class SettingsWindow extends javax.swing.JDialog
{
    private final String configFile = "./conf/settings.properties";	// Config file name
    private final String themeFile = "./conf/theme.properties";
    private NimRODTheme theme;
    private NimRODTheme oldTheme;
    private NimRODLookAndFeel lookAndFeel;
    private final ArrayList<String> listData = new ArrayList<String>( );
    private Boolean alwaysShowShimejiChooser = false;
    private Boolean alwaysShowInformationScreen = false;
    private String filter = "nearest";
    private double scaling = 1.0;
    private double opacity = 1.0;
    private Boolean windowedMode = false;
    private Dimension windowSize = new Dimension( 600, 500 );
    private Dimension buttonSize;
    private Dimension aboutButtonSize;
    private Color backgroundColour = new Color( 0, 255, 0 );
    private String backgroundMode = "centre";
    private String backgroundImage = null;
    private final String[ ] backgroundModes = { "centre", "fill", "fit", "stretch" };
    private Color primaryColour1;
    private Color primaryColour2;
    private Color primaryColour3;
    private Color secondaryColour1;
    private Color secondaryColour2;
    private Color secondaryColour3;
    private Color blackColour;
    private Color whiteColour;
    private Font font;
    private double menuOpacity = 1.0;
    
    private Boolean imageReloadRequired = false;
    private Boolean interactiveWindowReloadRequired = false;
    private Boolean environmentReloadRequired = false;
    
    /**
     * Creates new form SettingsWindow
     */    
    public SettingsWindow( java.awt.Frame parent, boolean modal )
    {
        super( parent, modal );
        initComponents( );
    }
    
    public void init( )
    {
        // initialise controls
        setLocationRelativeTo( null );
        grpFilter.add( radFilterNearest );
        grpFilter.add( radFilterBicubic );
        grpFilter.add( radFilterHqx );
        java.util.Hashtable<Integer,JLabel> labelTable = new java.util.Hashtable<Integer,JLabel>( );
        for( int index = 0; index < 9; index++ )
            labelTable.put( index * 10, new JLabel( index + "x" ) );
        sldScaling.setLabelTable( labelTable );
        sldScaling.setPaintLabels( true );
        sldScaling.setSnapToTicks( true );
        
        // load existing settings
        Properties properties = Main.getInstance( ).getProperties( );
        alwaysShowShimejiChooser = Boolean.parseBoolean( properties.getProperty( "AlwaysShowShimejiChooser", "false" ) );
        alwaysShowInformationScreen = Boolean.parseBoolean( properties.getProperty( "AlwaysShowInformationScreen", "false" ) );
        String filterText = Main.getInstance( ).getProperties( ).getProperty( "Filter", "false" );
        filter = "nearest";
        if( filterText.equalsIgnoreCase( "true" ) || filterText.equalsIgnoreCase( "hqx" ) )
            filter = "hqx";
        else if( filterText.equalsIgnoreCase( "bicubic" ) )
            filter = "bicubic";
        opacity = Double.parseDouble( properties.getProperty( "Opacity", "1.0" ) );
        scaling = Double.parseDouble( properties.getProperty( "Scaling", "1.0" ) );
        windowedMode = properties.getProperty( "Environment", "generic" ).equals( "virtual" );
        String[ ] windowArray = properties.getProperty( "WindowSize", "600x500" ).split( "x" );
        windowSize = new Dimension( Integer.parseInt( windowArray[ 0 ] ), Integer.parseInt( windowArray[ 1 ] ) );
        buttonSize = btnDone.getPreferredSize( );
        aboutButtonSize = btnWebsite.getPreferredSize( );
        backgroundColour = Color.decode( properties.getProperty( "Background", "#00FF00" ) );
        backgroundImage = properties.getProperty( "BackgroundImage", "" );
        backgroundMode = properties.getProperty( "BackgroundMode", "centre" );
        float menuScaling = Float.parseFloat( properties.getProperty( "MenuDPI", "96" ) ) / 96;
        chkAlwaysShowShimejiChooser.setSelected( alwaysShowShimejiChooser );
        chkAlwaysShowInformationScreen.setSelected( alwaysShowInformationScreen );
        if( filter.equals( "bicubic" ) )
            radFilterBicubic.setSelected( true );
        else if( filter.equals( "hqx" ) )
            radFilterHqx.setSelected( true );
        else
            radFilterNearest.setSelected( true );
        sldOpacity.setValue( (int)( opacity * 100 ) );
        sldScaling.setValue( (int)( scaling * 10 ) );
        
        listData.addAll( Arrays.asList( properties.getProperty( "InteractiveWindows", "" ).split( "/" ) ) );
        lstInteractiveWindows.setListData( listData.toArray( ) );
        
        Properties themeProperties = new Properties( );
        FileInputStream input;
        try
        {
            input = new FileInputStream( themeFile );
            themeProperties.load( input );
        }
        catch( FileNotFoundException ex )
        {
        }
        catch( IOException ex )
        {
        }
        primaryColour1 = Color.decode( themeProperties.getProperty( "nimrodlf.p1", "#1EA6EB" ) );
        primaryColour2 = Color.decode( themeProperties.getProperty( "nimrodlf.p2", "#28B0F5" ) );
        primaryColour3 = Color.decode( themeProperties.getProperty( "nimrodlf.p3", "#32BAFF" ) );
        secondaryColour1 = Color.decode( themeProperties.getProperty( "nimrodlf.s1", "#BCBCBE" ) );
        secondaryColour2 = Color.decode( themeProperties.getProperty( "nimrodlf.s2", "#C6C6C8" ) );
        secondaryColour3 = Color.decode( themeProperties.getProperty( "nimrodlf.s3", "#D0D0D2" ) );
        blackColour = Color.decode( themeProperties.getProperty( "nimrodlf.b", "#000000" ) );
        whiteColour = Color.decode( themeProperties.getProperty( "nimrodlf.w", "#FFFFFF" ) );
        menuOpacity = Integer.parseInt( properties.getProperty( "nimrodlf.menuOpacity", "255" ) ) / 255;
        font = Font.decode( themeProperties.getProperty( "nimrodlf.font", "SansSerif-PLAIN-12" ) );
        font = font.deriveFont( font.getSize( ) * menuScaling );
        pnlPrimaryColour1Preview.setBackground( primaryColour1 );
        txtPrimaryColour1.setText( String.format( "#%02X%02X%02X", primaryColour1.getRed( ), primaryColour1.getGreen( ), primaryColour1.getBlue( ) ) );
        pnlPrimaryColour2Preview.setBackground( primaryColour2 );
        txtPrimaryColour2.setText( String.format( "#%02X%02X%02X", primaryColour2.getRed( ), primaryColour2.getGreen( ), primaryColour2.getBlue( ) ) );
        pnlPrimaryColour3Preview.setBackground( primaryColour3 );
        txtPrimaryColour3.setText( String.format( "#%02X%02X%02X", primaryColour3.getRed( ), primaryColour2.getGreen( ), primaryColour3.getBlue( ) ) );
        pnlSecondaryColour1Preview.setBackground( secondaryColour1 );
        txtSecondaryColour1.setText( String.format( "#%02X%02X%02X", secondaryColour1.getRed( ), secondaryColour1.getGreen( ), secondaryColour1.getBlue( ) ) );
        pnlSecondaryColour2Preview.setBackground( secondaryColour2 );
        txtSecondaryColour2.setText( String.format( "#%02X%02X%02X", secondaryColour2.getRed( ), secondaryColour2.getGreen( ), secondaryColour2.getBlue( ) ) );
        pnlSecondaryColour3Preview.setBackground( secondaryColour3 );
        txtSecondaryColour3.setText( String.format( "#%02X%02X%02X", secondaryColour3.getRed( ), secondaryColour3.getGreen( ), secondaryColour3.getBlue( ) ) );
        pnlBlackColourPreview.setBackground( blackColour );
        txtBlackColour.setText( String.format( "#%02X%02X%02X", blackColour.getRed( ), blackColour.getGreen( ), blackColour.getBlue( ) ) );
        pnlWhiteColourPreview.setBackground( whiteColour );
        txtWhiteColour.setText( String.format( "#%02X%02X%02X", whiteColour.getRed( ), whiteColour.getGreen( ), whiteColour.getBlue( ) ) );
        theme = new NimRODTheme( );
        theme.setPrimary1( primaryColour1 );
        theme.setPrimary2( primaryColour2 );
        theme.setPrimary3( primaryColour3 );
        theme.setSecondary1( secondaryColour1 );
        theme.setSecondary2( secondaryColour2 );
        theme.setSecondary3( secondaryColour3 );
        theme.setBlack( blackColour );
        theme.setWhite( whiteColour );
        sldMenuOpacity.setValue( (int)( menuOpacity * 100 ) );
        theme.setFont( font );
        oldTheme = new NimRODTheme( );
        oldTheme.setPrimary1( primaryColour1 );
        oldTheme.setPrimary2( primaryColour2 );
        oldTheme.setPrimary3( primaryColour3 );
        oldTheme.setSecondary1( secondaryColour1 );
        oldTheme.setSecondary2( secondaryColour2 );
        oldTheme.setSecondary3( secondaryColour3 );
        oldTheme.setBlack( blackColour );
        oldTheme.setWhite( whiteColour );
        oldTheme.setMenuOpacity( (int)( menuOpacity * 255 ) );
        oldTheme.setFont( font );
        lookAndFeel = (NimRODLookAndFeel)UIManager.getLookAndFeel( );
        
        chkWindowModeEnabled.setSelected( windowedMode );
        spnWindowWidth.setBackground( txtBackgroundColour.getBackground( ) );
        spnWindowHeight.setBackground( txtBackgroundColour.getBackground( ) );
        spnWindowWidth.setEnabled( windowedMode );
        spnWindowHeight.setEnabled( windowedMode );
        spnWindowWidth.setValue( windowSize.width );
        spnWindowHeight.setValue( windowSize.height );
        txtBackgroundColour.setText( String.format( "#%02X%02X%02X", backgroundColour.getRed( ), backgroundColour.getGreen( ), backgroundColour.getBlue( ) ) );
        btnBackgroundColourChange.setEnabled( windowedMode );
        btnBackgroundImageChange.setEnabled( windowedMode );
        pnlBackgroundPreview.setBackground( backgroundColour );
        if( backgroundImage != null )
        {
            try
            {
                Dimension size = pnlBackgroundImage.getPreferredSize( );
                refreshBackgroundImage( );
                pnlBackgroundImage.setPreferredSize( size );
            }
            catch( Exception e )
            {
                backgroundImage = null;
                lblBackgroundImage.setIcon( null );
            }
        }
        cmbBackgroundImageMode.setEnabled( windowedMode && backgroundImage != null );
        btnBackgroundImageRemove.setEnabled( windowedMode && backgroundImage != null );
        
        // localisation
        ResourceBundle language = Main.getInstance( ).getLanguageBundle( );
        setTitle( language.getString( "Settings" ) );
        pnlTabs.setTitleAt( 0, language.getString( "General" ) );
        pnlTabs.setTitleAt( 1, language.getString( "InteractiveWindows" ) );
        pnlTabs.setTitleAt( 2, language.getString( "Theme" ) );
        pnlTabs.setTitleAt( 3, language.getString( "WindowMode" ) );
        pnlTabs.setTitleAt( 4, language.getString( "About" ) );
        chkAlwaysShowShimejiChooser.setText( language.getString( "AlwaysShowShimejiChooser" ) );
        chkAlwaysShowInformationScreen.setText( language.getString( "AlwaysShowInformationScreen" ) );
        lblOpacity.setText( language.getString( "Opacity" ) );
        lblScaling.setText( language.getString( "Scaling" ) );
        lblFilter.setText( language.getString( "FilterOptions" ) );
        radFilterNearest.setText( language.getString( "NearestNeighbour" ) );
        radFilterHqx.setText( language.getString( "Filter" ) );
        radFilterBicubic.setText( language.getString( "BicubicFilter" ) );
        btnAddInteractiveWindow.setText( language.getString( "Add" ) );
        btnRemoveInteractiveWindow.setText( language.getString( "Remove" ) );
        lblPrimaryColour1.setText( language.getString( "PrimaryColour1" ) );
        lblPrimaryColour2.setText( language.getString( "PrimaryColour2" ) );
        lblPrimaryColour3.setText( language.getString( "PrimaryColour3" ) );
        lblSecondaryColour1.setText( language.getString( "SecondaryColour1" ) );
        lblSecondaryColour2.setText( language.getString( "SecondaryColour2" ) );
        lblSecondaryColour3.setText( language.getString( "SecondaryColour3" ) );
        lblBlackColour.setText( language.getString( "BlackColour" ) );
        lblWhiteColour.setText( language.getString( "WhiteColour" ) );
        lblMenuOpacity.setText( language.getString( "MenuOpacity" ) );
        btnChangeFont.setText( language.getString( "ChangeFont" ) );
        btnReset.setText( language.getString( "Reset" ) );
        chkWindowModeEnabled.setText( language.getString( "WindowedModeEnabled" ) );
        lblDimensions.setText( language.getString( "Dimensions" ) );
        lblBackground.setText( language.getString( "Background" ) );
        btnBackgroundColourChange.setText( language.getString( "Change" ) );
        btnBackgroundImageChange.setText( language.getString( "Change" ) );
        cmbBackgroundImageMode.addItem( language.getString( "BackgroundModeCentre" ) );
        cmbBackgroundImageMode.addItem( language.getString( "BackgroundModeFill" ) );
        cmbBackgroundImageMode.addItem( language.getString( "BackgroundModeFit" ) );
        cmbBackgroundImageMode.addItem( language.getString( "BackgroundModeStretch" ) );
        btnBackgroundImageRemove.setText( language.getString( "Remove" ) );
        lblShimejiEE.setText( language.getString( "ShimejiEE" ) );
        lblDevelopedBy.setText( language.getString( "DevelopedBy" ) );
        btnWebsite.setText( language.getString( "Website" ) );
        btnDone.setText( language.getString( "Done" ) );
        btnCancel.setText( language.getString( "Cancel" ) );
        
        // come back around to this one now that the dropdown is populated
        for( int index = 0; index < backgroundModes.length; index++ )
        {
            if( backgroundMode.equals( backgroundModes[ index ] ) )
            {
                cmbBackgroundImageMode.setSelectedIndex( index );
                break;
            }
        }
    }
    
    public boolean display( )
    {
        float menuScaling = Float.parseFloat( Main.getInstance( ).getProperties( ).getProperty( "MenuDPI", "96" ) ) / 96;
        
        // scale controls to fit
        getContentPane( ).setPreferredSize( new Dimension( (int)( 600 * menuScaling ), (int)( 497 * menuScaling ) ) );
        sldOpacity.setPreferredSize( new Dimension( (int)( sldOpacity.getPreferredSize( ).width * menuScaling ), (int)( sldOpacity.getPreferredSize( ).height * menuScaling ) ) );
        sldScaling.setPreferredSize( new Dimension( (int)( sldScaling.getPreferredSize( ).width * menuScaling ), (int)( sldScaling.getPreferredSize( ).height * menuScaling ) ) );
        btnAddInteractiveWindow.setPreferredSize( new Dimension( (int)( btnAddInteractiveWindow.getPreferredSize( ).width * menuScaling ), (int)( btnAddInteractiveWindow.getPreferredSize( ).height * menuScaling ) ) );
        btnRemoveInteractiveWindow.setPreferredSize( new Dimension( (int)( btnRemoveInteractiveWindow.getPreferredSize( ).width * menuScaling ), (int)( btnRemoveInteractiveWindow.getPreferredSize( ).height * menuScaling ) ) );
        pnlInteractiveButtons.setPreferredSize( new Dimension( pnlInteractiveButtons.getPreferredSize( ).width, btnAddInteractiveWindow.getPreferredSize( ).height + 6 ) );
        txtPrimaryColour1.setPreferredSize( new Dimension( (int)( txtPrimaryColour1.getPreferredSize( ).width * menuScaling ), (int)( txtPrimaryColour1.getPreferredSize( ).height * menuScaling ) ) );
        txtPrimaryColour2.setPreferredSize( new Dimension( (int)( txtPrimaryColour2.getPreferredSize( ).width * menuScaling ), (int)( txtPrimaryColour2.getPreferredSize( ).height * menuScaling ) ) );
        txtPrimaryColour3.setPreferredSize( new Dimension( (int)( txtPrimaryColour3.getPreferredSize( ).width * menuScaling ), (int)( txtPrimaryColour3.getPreferredSize( ).height * menuScaling ) ) );
        txtSecondaryColour1.setPreferredSize( new Dimension( (int)( txtSecondaryColour1.getPreferredSize( ).width * menuScaling ), (int)( txtSecondaryColour1.getPreferredSize( ).height * menuScaling ) ) );
        txtSecondaryColour2.setPreferredSize( new Dimension( (int)( txtSecondaryColour2.getPreferredSize( ).width * menuScaling ), (int)( txtSecondaryColour2.getPreferredSize( ).height * menuScaling ) ) );
        txtSecondaryColour3.setPreferredSize( new Dimension( (int)( txtSecondaryColour3.getPreferredSize( ).width * menuScaling ), (int)( txtSecondaryColour3.getPreferredSize( ).height * menuScaling ) ) );
        txtBlackColour.setPreferredSize( new Dimension( (int)( txtBlackColour.getPreferredSize( ).width * menuScaling ), (int)( txtBlackColour.getPreferredSize( ).height * menuScaling ) ) );
        txtWhiteColour.setPreferredSize( new Dimension( (int)( txtWhiteColour.getPreferredSize( ).width * menuScaling ), (int)( txtWhiteColour.getPreferredSize( ).height * menuScaling ) ) );
        pnlPrimaryColour1PreviewContainer.setPreferredSize( new Dimension( (int)( pnlPrimaryColour1PreviewContainer.getPreferredSize( ).width * menuScaling ), (int)( pnlPrimaryColour1PreviewContainer.getPreferredSize( ).height * menuScaling ) ) );
        pnlPrimaryColour1Preview.setPreferredSize( new Dimension( (int)( pnlPrimaryColour1Preview.getPreferredSize( ).width * menuScaling ), (int)( pnlPrimaryColour1Preview.getPreferredSize( ).height * menuScaling ) ) );
        pnlPrimaryColour2PreviewContainer.setPreferredSize( new Dimension( (int)( pnlPrimaryColour2PreviewContainer.getPreferredSize( ).width * menuScaling ), (int)( pnlPrimaryColour2PreviewContainer.getPreferredSize( ).height * menuScaling ) ) );
        pnlPrimaryColour2Preview.setPreferredSize( new Dimension( (int)( pnlPrimaryColour2Preview.getPreferredSize( ).width * menuScaling ), (int)( pnlPrimaryColour2Preview.getPreferredSize( ).height * menuScaling ) ) );
        pnlPrimaryColour3PreviewContainer.setPreferredSize( new Dimension( (int)( pnlPrimaryColour3PreviewContainer.getPreferredSize( ).width * menuScaling ), (int)( pnlPrimaryColour3PreviewContainer.getPreferredSize( ).height * menuScaling ) ) );
        pnlPrimaryColour3Preview.setPreferredSize( new Dimension( (int)( pnlPrimaryColour3Preview.getPreferredSize( ).width * menuScaling ), (int)( pnlPrimaryColour3Preview.getPreferredSize( ).height * menuScaling ) ) );
        pnlSecondaryColour1PreviewContainer.setPreferredSize( new Dimension( (int)( pnlSecondaryColour1PreviewContainer.getPreferredSize( ).width * menuScaling ), (int)( pnlSecondaryColour1PreviewContainer.getPreferredSize( ).height * menuScaling ) ) );
        pnlSecondaryColour1Preview.setPreferredSize( new Dimension( (int)( pnlSecondaryColour1Preview.getPreferredSize( ).width * menuScaling ), (int)( pnlSecondaryColour1Preview.getPreferredSize( ).height * menuScaling ) ) );
        pnlSecondaryColour2PreviewContainer.setPreferredSize( new Dimension( (int)( pnlSecondaryColour2PreviewContainer.getPreferredSize( ).width * menuScaling ), (int)( pnlSecondaryColour2PreviewContainer.getPreferredSize( ).height * menuScaling ) ) );
        pnlSecondaryColour2Preview.setPreferredSize( new Dimension( (int)( pnlSecondaryColour2Preview.getPreferredSize( ).width * menuScaling ), (int)( pnlSecondaryColour2Preview.getPreferredSize( ).height * menuScaling ) ) );
        pnlSecondaryColour3PreviewContainer.setPreferredSize( new Dimension( (int)( pnlSecondaryColour3PreviewContainer.getPreferredSize( ).width * menuScaling ), (int)( pnlSecondaryColour3PreviewContainer.getPreferredSize( ).height * menuScaling ) ) );
        pnlSecondaryColour3Preview.setPreferredSize( new Dimension( (int)( pnlSecondaryColour3Preview.getPreferredSize( ).width * menuScaling ), (int)( pnlSecondaryColour3Preview.getPreferredSize( ).height * menuScaling ) ) );
        pnlBlackColourPreviewContainer.setPreferredSize( new Dimension( (int)( pnlBlackColourPreviewContainer.getPreferredSize( ).width * menuScaling ), (int)( pnlBlackColourPreviewContainer.getPreferredSize( ).height * menuScaling ) ) );
        pnlBlackColourPreview.setPreferredSize( new Dimension( (int)( pnlBlackColourPreview.getPreferredSize( ).width * menuScaling ), (int)( pnlBlackColourPreview.getPreferredSize( ).height * menuScaling ) ) );
        pnlWhiteColourPreviewContainer.setPreferredSize( new Dimension( (int)( pnlWhiteColourPreviewContainer.getPreferredSize( ).width * menuScaling ), (int)( pnlWhiteColourPreviewContainer.getPreferredSize( ).height * menuScaling ) ) );
        pnlWhiteColourPreview.setPreferredSize( new Dimension( (int)( pnlWhiteColourPreview.getPreferredSize( ).width * menuScaling ), (int)( pnlWhiteColourPreview.getPreferredSize( ).height * menuScaling ) ) );
        btnPrimaryColour1Change.setPreferredSize( new Dimension( (int)( btnPrimaryColour1Change.getPreferredSize( ).width * menuScaling ), (int)( btnPrimaryColour1Change.getPreferredSize( ).height * menuScaling ) ) );
        btnPrimaryColour2Change.setPreferredSize( new Dimension( (int)( btnPrimaryColour2Change.getPreferredSize( ).width * menuScaling ), (int)( btnPrimaryColour2Change.getPreferredSize( ).height * menuScaling ) ) );
        btnPrimaryColour3Change.setPreferredSize( new Dimension( (int)( btnPrimaryColour3Change.getPreferredSize( ).width * menuScaling ), (int)( btnPrimaryColour3Change.getPreferredSize( ).height * menuScaling ) ) );
        btnSecondaryColour1Change.setPreferredSize( new Dimension( (int)( btnSecondaryColour1Change.getPreferredSize( ).width * menuScaling ), (int)( btnSecondaryColour1Change.getPreferredSize( ).height * menuScaling ) ) );
        btnSecondaryColour2Change.setPreferredSize( new Dimension( (int)( btnSecondaryColour2Change.getPreferredSize( ).width * menuScaling ), (int)( btnSecondaryColour2Change.getPreferredSize( ).height * menuScaling ) ) );
        btnSecondaryColour3Change.setPreferredSize( new Dimension( (int)( btnSecondaryColour3Change.getPreferredSize( ).width * menuScaling ), (int)( btnSecondaryColour3Change.getPreferredSize( ).height * menuScaling ) ) );
        btnBlackColourChange.setPreferredSize( new Dimension( (int)( btnBlackColourChange.getPreferredSize( ).width * menuScaling ), (int)( btnBlackColourChange.getPreferredSize( ).height * menuScaling ) ) );
        btnWhiteColourChange.setPreferredSize( new Dimension( (int)( btnWhiteColourChange.getPreferredSize( ).width * menuScaling ), (int)( btnWhiteColourChange.getPreferredSize( ).height * menuScaling ) ) );
        sldMenuOpacity.setPreferredSize( new Dimension( (int)( sldMenuOpacity.getPreferredSize( ).width * menuScaling ), (int)( sldMenuOpacity.getPreferredSize( ).height * menuScaling ) ) );
        btnChangeFont.setPreferredSize( new Dimension( (int)( btnChangeFont.getPreferredSize( ).width * menuScaling ), (int)( btnChangeFont.getPreferredSize( ).height * menuScaling ) ) );
        btnReset.setPreferredSize( new Dimension( (int)( btnReset.getPreferredSize( ).width * menuScaling ), (int)( btnReset.getPreferredSize( ).height * menuScaling ) ) );
        pnlThemeButtons.setPreferredSize( new Dimension( pnlThemeButtons.getPreferredSize( ).width, btnReset.getPreferredSize( ).height + 6 ) );
        spnWindowWidth.setPreferredSize( new Dimension( (int)( spnWindowWidth.getPreferredSize( ).width * menuScaling ), (int)( spnWindowWidth.getPreferredSize( ).height * menuScaling ) ) );
        spnWindowHeight.setPreferredSize( new Dimension( (int)( spnWindowHeight.getPreferredSize( ).width * menuScaling ), (int)( spnWindowHeight.getPreferredSize( ).height * menuScaling ) ) );
        txtBackgroundColour.setPreferredSize( new Dimension( (int)( txtBackgroundColour.getPreferredSize( ).width * menuScaling ), (int)( txtBackgroundColour.getPreferredSize( ).height * menuScaling ) ) );
        pnlBackgroundPreviewContainer.setPreferredSize( new Dimension( (int)( pnlBackgroundPreviewContainer.getPreferredSize( ).width * menuScaling ), (int)( pnlBackgroundPreviewContainer.getPreferredSize( ).height * menuScaling ) ) );
        pnlBackgroundPreview.setPreferredSize( new Dimension( (int)( pnlBackgroundPreview.getPreferredSize( ).width * menuScaling ), (int)( pnlBackgroundPreview.getPreferredSize( ).height * menuScaling ) ) );
        btnBackgroundColourChange.setPreferredSize( new Dimension( (int)( btnBackgroundColourChange.getPreferredSize( ).width * menuScaling ), (int)( btnBackgroundColourChange.getPreferredSize( ).height * menuScaling ) ) );
        btnBackgroundImageChange.setPreferredSize( new Dimension( (int)( btnBackgroundImageChange.getPreferredSize( ).width * menuScaling ), (int)( btnBackgroundImageChange.getPreferredSize( ).height * menuScaling ) ) );
        btnBackgroundImageRemove.setPreferredSize( new Dimension( (int)( btnBackgroundImageRemove.getPreferredSize( ).width * menuScaling ), (int)( btnBackgroundImageRemove.getPreferredSize( ).height * menuScaling ) ) );
        cmbBackgroundImageMode.setPreferredSize( btnBackgroundImageRemove.getPreferredSize( ) );
        pnlBackgroundImage.setPreferredSize( new Dimension( (int)( pnlBackgroundImage.getPreferredSize( ).width * menuScaling ), (int)( pnlBackgroundImage.getPreferredSize( ).height * menuScaling ) ) );
        pnlBackgroundImage.setMaximumSize( pnlBackgroundImage.getPreferredSize( ) );
        lblIcon.setPreferredSize( new Dimension( (int)( lblIcon.getPreferredSize( ).width * menuScaling ), (int)( lblIcon.getPreferredSize( ).height * menuScaling ) ) );
        lblIcon.setMaximumSize( lblIcon.getPreferredSize( ) );
        if( getIconImages( ).size( ) > 0 )
            lblIcon.setIcon( new ImageIcon( getIconImages( ).get( 0 ).getScaledInstance( lblIcon.getPreferredSize( ).width, lblIcon.getPreferredSize( ).height, java.awt.Image.SCALE_DEFAULT ) ) );
        btnWebsite.setPreferredSize( new Dimension( (int)( btnWebsite.getPreferredSize( ).width * menuScaling ), (int)( btnWebsite.getPreferredSize( ).height * menuScaling ) ) );
        btnDiscord.setPreferredSize( new Dimension( (int)( btnDiscord.getPreferredSize( ).width * menuScaling ), (int)( btnDiscord.getPreferredSize( ).height * menuScaling ) ) );
        btnPatreon.setPreferredSize( new Dimension( (int)( btnPatreon.getPreferredSize( ).width * menuScaling ), (int)( btnPatreon.getPreferredSize( ).height * menuScaling ) ) );
        pnlAboutButtons.setPreferredSize( new Dimension( pnlAboutButtons.getPreferredSize( ).width, btnWebsite.getPreferredSize( ).height + 6 ) );
        btnDone.setPreferredSize( new Dimension( (int)( btnDone.getPreferredSize( ).width * menuScaling ), (int)( btnDone.getPreferredSize( ).height * menuScaling ) ) );
        btnCancel.setPreferredSize( new Dimension( (int)( btnCancel.getPreferredSize( ).width * menuScaling ), (int)( btnCancel.getPreferredSize( ).height * menuScaling ) ) );
        pnlFooter.setPreferredSize( new Dimension( pnlFooter.getPreferredSize( ).width, btnDone.getPreferredSize( ).height + 6 ) );
        pack( );
        setVisible( true );
        
        return true;
    }
    
    private void browseToUrl( String url )
    {
        try
        {
            Desktop desktop = Desktop.isDesktopSupported( ) ? Desktop.getDesktop( ) : null;
            if( desktop != null && desktop.isSupported( Desktop.Action.BROWSE ) )
                desktop.browse( new URI( url ) );
            else
                throw new UnsupportedOperationException( Main.getInstance( ).getLanguageBundle( ).getString( "FailedOpenWebBrowserErrorMessage" ) + " " + url );
        }
        catch( Exception e )
        {
            JOptionPane.showMessageDialog( this, e.getMessage( ), "Error", JOptionPane.PLAIN_MESSAGE );
        }
    }
    
    public boolean getEnvironmentReloadRequired( )
    {
        return environmentReloadRequired;
    }
    
    public boolean getImageReloadRequired( )
    {
        return imageReloadRequired;
    }
    
    public boolean getInteractiveWindowReloadRequired( )
    {
        return interactiveWindowReloadRequired;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings( "unchecked" )
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        grpFilter = new javax.swing.ButtonGroup();
        pnlTabs = new javax.swing.JTabbedPane();
        pnlGeneral = new javax.swing.JPanel();
        chkAlwaysShowShimejiChooser = new javax.swing.JCheckBox();
        lblScaling = new javax.swing.JLabel();
        sldScaling = new javax.swing.JSlider();
        lblFilter = new javax.swing.JLabel();
        radFilterNearest = new javax.swing.JRadioButton();
        radFilterBicubic = new javax.swing.JRadioButton();
        radFilterHqx = new javax.swing.JRadioButton();
        sldOpacity = new javax.swing.JSlider();
        lblOpacity = new javax.swing.JLabel();
        chkAlwaysShowInformationScreen = new javax.swing.JCheckBox();
        pnlInteractiveWindows = new javax.swing.JPanel();
        pnlInteractiveButtons = new javax.swing.JPanel();
        btnAddInteractiveWindow = new javax.swing.JButton();
        btnRemoveInteractiveWindow = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstInteractiveWindows = new javax.swing.JList();
        pnlTheme = new javax.swing.JPanel();
        pnlThemeButtons = new javax.swing.JPanel();
        btnChangeFont = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        lblPrimaryColour1 = new javax.swing.JLabel();
        txtPrimaryColour1 = new javax.swing.JTextField();
        pnlPrimaryColour1PreviewContainer = new javax.swing.JPanel();
        gluePrimaryColour1a = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        pnlPrimaryColour1Preview = new javax.swing.JPanel();
        gluePrimaryColour1b = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        btnPrimaryColour1Change = new javax.swing.JButton();
        lblPrimaryColour2 = new javax.swing.JLabel();
        txtPrimaryColour2 = new javax.swing.JTextField();
        pnlPrimaryColour2PreviewContainer = new javax.swing.JPanel();
        gluePrimaryColour2a = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        pnlPrimaryColour2Preview = new javax.swing.JPanel();
        gluePrimaryColour2b = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        btnPrimaryColour2Change = new javax.swing.JButton();
        lblPrimaryColour3 = new javax.swing.JLabel();
        txtPrimaryColour3 = new javax.swing.JTextField();
        pnlPrimaryColour3PreviewContainer = new javax.swing.JPanel();
        gluePrimaryColour3a = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        pnlPrimaryColour3Preview = new javax.swing.JPanel();
        gluePrimaryColour3b = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        btnPrimaryColour3Change = new javax.swing.JButton();
        lblSecondaryColour1 = new javax.swing.JLabel();
        txtSecondaryColour1 = new javax.swing.JTextField();
        pnlSecondaryColour1PreviewContainer = new javax.swing.JPanel();
        glueSecondaryColour1a = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        pnlSecondaryColour1Preview = new javax.swing.JPanel();
        glueSecondaryColour1b = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        btnSecondaryColour1Change = new javax.swing.JButton();
        lblSecondaryColour2 = new javax.swing.JLabel();
        txtSecondaryColour2 = new javax.swing.JTextField();
        pnlSecondaryColour2PreviewContainer = new javax.swing.JPanel();
        glueSecondaryColour2a = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        pnlSecondaryColour2Preview = new javax.swing.JPanel();
        glueSecondaryColour2b = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        btnSecondaryColour2Change = new javax.swing.JButton();
        lblSecondaryColour3 = new javax.swing.JLabel();
        txtSecondaryColour3 = new javax.swing.JTextField();
        pnlSecondaryColour3PreviewContainer = new javax.swing.JPanel();
        glueSecondaryColour3a = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        pnlSecondaryColour3Preview = new javax.swing.JPanel();
        glueSecondaryColour3b = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        btnSecondaryColour3Change = new javax.swing.JButton();
        lblMenuOpacity = new javax.swing.JLabel();
        sldMenuOpacity = new javax.swing.JSlider();
        lblBlackColour = new javax.swing.JLabel();
        txtBlackColour = new javax.swing.JTextField();
        pnlBlackColourPreviewContainer = new javax.swing.JPanel();
        glueBlackColoura = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        pnlBlackColourPreview = new javax.swing.JPanel();
        glueBlackColourb = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        btnBlackColourChange = new javax.swing.JButton();
        lblWhiteColour = new javax.swing.JLabel();
        txtWhiteColour = new javax.swing.JTextField();
        pnlWhiteColourPreviewContainer = new javax.swing.JPanel();
        glueWhiteColoura = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        pnlWhiteColourPreview = new javax.swing.JPanel();
        glueWhiteColourb = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        btnWhiteColourChange = new javax.swing.JButton();
        pnlWindowMode = new javax.swing.JPanel();
        chkWindowModeEnabled = new javax.swing.JCheckBox();
        lblDimensions = new javax.swing.JLabel();
        lblDimensionsX = new javax.swing.JLabel();
        lblBackground = new javax.swing.JLabel();
        txtBackgroundColour = new javax.swing.JTextField();
        btnBackgroundColourChange = new javax.swing.JButton();
        spnWindowWidth = new javax.swing.JSpinner();
        spnWindowHeight = new javax.swing.JSpinner();
        lblBackgroundColour = new javax.swing.JLabel();
        pnlBackgroundPreviewContainer = new javax.swing.JPanel();
        glueBackground = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        pnlBackgroundPreview = new javax.swing.JPanel();
        glueBackground2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        lblBackgroundImageCaption = new javax.swing.JLabel();
        pnlBackgroundImage = new javax.swing.JPanel();
        lblBackgroundImage = new javax.swing.JLabel();
        btnBackgroundImageChange = new javax.swing.JButton();
        btnBackgroundImageRemove = new javax.swing.JButton();
        cmbBackgroundImageMode = new javax.swing.JComboBox<String>();
        pnlAbout = new javax.swing.JPanel();
        glue1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        lblIcon = new javax.swing.JLabel();
        rigid1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 15), new java.awt.Dimension(0, 15), new java.awt.Dimension(0, 15));
        lblShimejiEE = new javax.swing.JLabel();
        rigid2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 5), new java.awt.Dimension(0, 10));
        lblVersion = new javax.swing.JLabel();
        rigid3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 15), new java.awt.Dimension(0, 15), new java.awt.Dimension(0, 15));
        lblDevelopedBy = new javax.swing.JLabel();
        lblKilkakon = new javax.swing.JLabel();
        rigid4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 30), new java.awt.Dimension(0, 30), new java.awt.Dimension(0, 30));
        pnlAboutButtons = new javax.swing.JPanel();
        btnWebsite = new javax.swing.JButton();
        btnDiscord = new javax.swing.JButton();
        btnPatreon = new javax.swing.JButton();
        glue2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        pnlFooter = new javax.swing.JPanel();
        btnDone = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        chkAlwaysShowShimejiChooser.setText("Always Show Shimeji Chooser");
        chkAlwaysShowShimejiChooser.addItemListener(new java.awt.event.ItemListener()
        {
            public void itemStateChanged(java.awt.event.ItemEvent evt)
            {
                chkAlwaysShowShimejiChooserItemStateChanged(evt);
            }
        });

        lblScaling.setText("Scaling");

        sldScaling.setMajorTickSpacing(10);
        sldScaling.setMaximum(80);
        sldScaling.setMinorTickSpacing(5);
        sldScaling.setPaintLabels(true);
        sldScaling.setPaintTicks(true);
        sldScaling.setSnapToTicks(true);
        sldScaling.setValue(10);
        sldScaling.setPreferredSize(new java.awt.Dimension(300, 45));
        sldScaling.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                sldScalingStateChanged(evt);
            }
        });

        lblFilter.setText("Filter");

        radFilterNearest.setText("Nearest");
        radFilterNearest.addItemListener(new java.awt.event.ItemListener()
        {
            public void itemStateChanged(java.awt.event.ItemEvent evt)
            {
                radFilterItemStateChanged(evt);
            }
        });

        radFilterBicubic.setText("Bicubic");
        radFilterBicubic.addItemListener(new java.awt.event.ItemListener()
        {
            public void itemStateChanged(java.awt.event.ItemEvent evt)
            {
                radFilterItemStateChanged(evt);
            }
        });

        radFilterHqx.setText("hqx");
        radFilterHqx.addItemListener(new java.awt.event.ItemListener()
        {
            public void itemStateChanged(java.awt.event.ItemEvent evt)
            {
                radFilterItemStateChanged(evt);
            }
        });

        sldOpacity.setMajorTickSpacing(10);
        sldOpacity.setMinorTickSpacing(5);
        sldOpacity.setPaintLabels(true);
        sldOpacity.setPaintTicks(true);
        sldOpacity.setSnapToTicks(true);
        sldOpacity.setValue(10);
        sldOpacity.setPreferredSize(new java.awt.Dimension(300, 45));
        sldOpacity.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                sldOpacityStateChanged(evt);
            }
        });

        lblOpacity.setText("Opacity");

        chkAlwaysShowInformationScreen.setText("Always Show Information Screen");
        chkAlwaysShowInformationScreen.addItemListener(new java.awt.event.ItemListener()
        {
            public void itemStateChanged(java.awt.event.ItemEvent evt)
            {
                chkAlwaysShowInformationScreenItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlGeneralLayout = new javax.swing.GroupLayout(pnlGeneral);
        pnlGeneral.setLayout(pnlGeneralLayout);
        pnlGeneralLayout.setHorizontalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkAlwaysShowShimejiChooser)
                    .addComponent(lblFilter)
                    .addComponent(lblScaling)
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(sldOpacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(radFilterNearest)
                                .addComponent(sldScaling, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(radFilterBicubic)
                                .addComponent(radFilterHqx))))
                    .addComponent(lblOpacity)
                    .addComponent(chkAlwaysShowInformationScreen))
                .addContainerGap(80, Short.MAX_VALUE))
        );
        pnlGeneralLayout.setVerticalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkAlwaysShowShimejiChooser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkAlwaysShowInformationScreen)
                .addGap(18, 18, 18)
                .addComponent(lblOpacity)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sldOpacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblScaling)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sldScaling, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radFilterNearest)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radFilterBicubic)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radFilterHqx)
                .addContainerGap(75, Short.MAX_VALUE))
        );

        pnlTabs.addTab("General", pnlGeneral);

        pnlInteractiveButtons.setPreferredSize(new java.awt.Dimension(380, 36));
        pnlInteractiveButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        btnAddInteractiveWindow.setText("Add");
        btnAddInteractiveWindow.setMaximumSize(new java.awt.Dimension(130, 26));
        btnAddInteractiveWindow.setMinimumSize(new java.awt.Dimension(95, 23));
        btnAddInteractiveWindow.setName(""); // NOI18N
        btnAddInteractiveWindow.setPreferredSize(new java.awt.Dimension(130, 26));
        btnAddInteractiveWindow.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnAddInteractiveWindowActionPerformed(evt);
            }
        });
        pnlInteractiveButtons.add(btnAddInteractiveWindow);

        btnRemoveInteractiveWindow.setText("Remove");
        btnRemoveInteractiveWindow.setMaximumSize(new java.awt.Dimension(130, 26));
        btnRemoveInteractiveWindow.setMinimumSize(new java.awt.Dimension(95, 23));
        btnRemoveInteractiveWindow.setPreferredSize(new java.awt.Dimension(130, 26));
        btnRemoveInteractiveWindow.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnRemoveInteractiveWindowActionPerformed(evt);
            }
        });
        pnlInteractiveButtons.add(btnRemoveInteractiveWindow);

        lstInteractiveWindows.setModel(new javax.swing.AbstractListModel()
        {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(lstInteractiveWindows);

        javax.swing.GroupLayout pnlInteractiveWindowsLayout = new javax.swing.GroupLayout(pnlInteractiveWindows);
        pnlInteractiveWindows.setLayout(pnlInteractiveWindowsLayout);
        pnlInteractiveWindowsLayout.setHorizontalGroup(
            pnlInteractiveWindowsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlInteractiveWindowsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlInteractiveWindowsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addComponent(pnlInteractiveButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlInteractiveWindowsLayout.setVerticalGroup(
            pnlInteractiveWindowsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlInteractiveWindowsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlInteractiveButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlTabs.addTab("InteractiveWindows", pnlInteractiveWindows);

        pnlThemeButtons.setPreferredSize(new java.awt.Dimension(380, 36));
        pnlThemeButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        btnChangeFont.setText("Change Font");
        btnChangeFont.setMaximumSize(new java.awt.Dimension(130, 26));
        btnChangeFont.setName(""); // NOI18N
        btnChangeFont.setPreferredSize(new java.awt.Dimension(130, 26));
        btnChangeFont.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnChangeFontActionPerformed(evt);
            }
        });
        pnlThemeButtons.add(btnChangeFont);

        btnReset.setText("Reset");
        btnReset.setMaximumSize(new java.awt.Dimension(130, 26));
        btnReset.setMinimumSize(new java.awt.Dimension(95, 23));
        btnReset.setPreferredSize(new java.awt.Dimension(130, 26));
        btnReset.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnResetActionPerformed(evt);
            }
        });
        pnlThemeButtons.add(btnReset);

        lblPrimaryColour1.setText("Primary 1");

        txtPrimaryColour1.setEditable(false);
        txtPrimaryColour1.setText("#00FF00");
        txtPrimaryColour1.setPreferredSize(new java.awt.Dimension(70, 24));

        pnlPrimaryColour1PreviewContainer.setLayout(new javax.swing.BoxLayout(pnlPrimaryColour1PreviewContainer, javax.swing.BoxLayout.Y_AXIS));
        pnlPrimaryColour1PreviewContainer.add(gluePrimaryColour1a);

        pnlPrimaryColour1Preview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlPrimaryColour1Preview.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout pnlPrimaryColour1PreviewLayout = new javax.swing.GroupLayout(pnlPrimaryColour1Preview);
        pnlPrimaryColour1Preview.setLayout(pnlPrimaryColour1PreviewLayout);
        pnlPrimaryColour1PreviewLayout.setHorizontalGroup(
            pnlPrimaryColour1PreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlPrimaryColour1PreviewLayout.setVerticalGroup(
            pnlPrimaryColour1PreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlPrimaryColour1PreviewContainer.add(pnlPrimaryColour1Preview);
        pnlPrimaryColour1PreviewContainer.add(gluePrimaryColour1b);

        btnPrimaryColour1Change.setText("Change");
        btnPrimaryColour1Change.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnPrimaryColour1ChangeActionPerformed(evt);
            }
        });

        lblPrimaryColour2.setText("Primary 2");

        txtPrimaryColour2.setEditable(false);
        txtPrimaryColour2.setText("#00FF00");
        txtPrimaryColour2.setPreferredSize(new java.awt.Dimension(70, 24));

        pnlPrimaryColour2PreviewContainer.setLayout(new javax.swing.BoxLayout(pnlPrimaryColour2PreviewContainer, javax.swing.BoxLayout.Y_AXIS));
        pnlPrimaryColour2PreviewContainer.add(gluePrimaryColour2a);

        pnlPrimaryColour2Preview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlPrimaryColour2Preview.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout pnlPrimaryColour2PreviewLayout = new javax.swing.GroupLayout(pnlPrimaryColour2Preview);
        pnlPrimaryColour2Preview.setLayout(pnlPrimaryColour2PreviewLayout);
        pnlPrimaryColour2PreviewLayout.setHorizontalGroup(
            pnlPrimaryColour2PreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlPrimaryColour2PreviewLayout.setVerticalGroup(
            pnlPrimaryColour2PreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlPrimaryColour2PreviewContainer.add(pnlPrimaryColour2Preview);
        pnlPrimaryColour2PreviewContainer.add(gluePrimaryColour2b);

        btnPrimaryColour2Change.setText("Change");
        btnPrimaryColour2Change.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnPrimaryColour2ChangeActionPerformed(evt);
            }
        });

        lblPrimaryColour3.setText("Primary 3");

        txtPrimaryColour3.setEditable(false);
        txtPrimaryColour3.setText("#00FF00");
        txtPrimaryColour3.setPreferredSize(new java.awt.Dimension(70, 24));

        pnlPrimaryColour3PreviewContainer.setLayout(new javax.swing.BoxLayout(pnlPrimaryColour3PreviewContainer, javax.swing.BoxLayout.Y_AXIS));
        pnlPrimaryColour3PreviewContainer.add(gluePrimaryColour3a);

        pnlPrimaryColour3Preview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlPrimaryColour3Preview.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout pnlPrimaryColour3PreviewLayout = new javax.swing.GroupLayout(pnlPrimaryColour3Preview);
        pnlPrimaryColour3Preview.setLayout(pnlPrimaryColour3PreviewLayout);
        pnlPrimaryColour3PreviewLayout.setHorizontalGroup(
            pnlPrimaryColour3PreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlPrimaryColour3PreviewLayout.setVerticalGroup(
            pnlPrimaryColour3PreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlPrimaryColour3PreviewContainer.add(pnlPrimaryColour3Preview);
        pnlPrimaryColour3PreviewContainer.add(gluePrimaryColour3b);

        btnPrimaryColour3Change.setText("Change");
        btnPrimaryColour3Change.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnPrimaryColour3ChangeActionPerformed(evt);
            }
        });

        lblSecondaryColour1.setText("Secondary 1");

        txtSecondaryColour1.setEditable(false);
        txtSecondaryColour1.setText("#00FF00");
        txtSecondaryColour1.setPreferredSize(new java.awt.Dimension(70, 24));

        pnlSecondaryColour1PreviewContainer.setLayout(new javax.swing.BoxLayout(pnlSecondaryColour1PreviewContainer, javax.swing.BoxLayout.Y_AXIS));
        pnlSecondaryColour1PreviewContainer.add(glueSecondaryColour1a);

        pnlSecondaryColour1Preview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlSecondaryColour1Preview.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout pnlSecondaryColour1PreviewLayout = new javax.swing.GroupLayout(pnlSecondaryColour1Preview);
        pnlSecondaryColour1Preview.setLayout(pnlSecondaryColour1PreviewLayout);
        pnlSecondaryColour1PreviewLayout.setHorizontalGroup(
            pnlSecondaryColour1PreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlSecondaryColour1PreviewLayout.setVerticalGroup(
            pnlSecondaryColour1PreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlSecondaryColour1PreviewContainer.add(pnlSecondaryColour1Preview);
        pnlSecondaryColour1PreviewContainer.add(glueSecondaryColour1b);

        btnSecondaryColour1Change.setText("Change");
        btnSecondaryColour1Change.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnSecondaryColour1ChangeActionPerformed(evt);
            }
        });

        lblSecondaryColour2.setText("Secondary 2");

        txtSecondaryColour2.setEditable(false);
        txtSecondaryColour2.setText("#00FF00");
        txtSecondaryColour2.setPreferredSize(new java.awt.Dimension(70, 24));

        pnlSecondaryColour2PreviewContainer.setLayout(new javax.swing.BoxLayout(pnlSecondaryColour2PreviewContainer, javax.swing.BoxLayout.Y_AXIS));
        pnlSecondaryColour2PreviewContainer.add(glueSecondaryColour2a);

        pnlSecondaryColour2Preview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlSecondaryColour2Preview.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout pnlSecondaryColour2PreviewLayout = new javax.swing.GroupLayout(pnlSecondaryColour2Preview);
        pnlSecondaryColour2Preview.setLayout(pnlSecondaryColour2PreviewLayout);
        pnlSecondaryColour2PreviewLayout.setHorizontalGroup(
            pnlSecondaryColour2PreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlSecondaryColour2PreviewLayout.setVerticalGroup(
            pnlSecondaryColour2PreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlSecondaryColour2PreviewContainer.add(pnlSecondaryColour2Preview);
        pnlSecondaryColour2PreviewContainer.add(glueSecondaryColour2b);

        btnSecondaryColour2Change.setText("Change");
        btnSecondaryColour2Change.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnSecondaryColour2ChangeActionPerformed(evt);
            }
        });

        lblSecondaryColour3.setText("Secondary 3");

        txtSecondaryColour3.setEditable(false);
        txtSecondaryColour3.setText("#00FF00");
        txtSecondaryColour3.setPreferredSize(new java.awt.Dimension(70, 24));

        pnlSecondaryColour3PreviewContainer.setLayout(new javax.swing.BoxLayout(pnlSecondaryColour3PreviewContainer, javax.swing.BoxLayout.Y_AXIS));
        pnlSecondaryColour3PreviewContainer.add(glueSecondaryColour3a);

        pnlSecondaryColour3Preview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlSecondaryColour3Preview.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout pnlSecondaryColour3PreviewLayout = new javax.swing.GroupLayout(pnlSecondaryColour3Preview);
        pnlSecondaryColour3Preview.setLayout(pnlSecondaryColour3PreviewLayout);
        pnlSecondaryColour3PreviewLayout.setHorizontalGroup(
            pnlSecondaryColour3PreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlSecondaryColour3PreviewLayout.setVerticalGroup(
            pnlSecondaryColour3PreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlSecondaryColour3PreviewContainer.add(pnlSecondaryColour3Preview);
        pnlSecondaryColour3PreviewContainer.add(glueSecondaryColour3b);

        btnSecondaryColour3Change.setText("Change");
        btnSecondaryColour3Change.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnSecondaryColour3ChangeActionPerformed(evt);
            }
        });

        lblMenuOpacity.setText("Menu Opacity");

        sldMenuOpacity.setMajorTickSpacing(10);
        sldMenuOpacity.setMinorTickSpacing(5);
        sldMenuOpacity.setPaintLabels(true);
        sldMenuOpacity.setPaintTicks(true);
        sldMenuOpacity.setSnapToTicks(true);
        sldMenuOpacity.setValue(10);
        sldMenuOpacity.setPreferredSize(new java.awt.Dimension(300, 45));
        sldMenuOpacity.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                sldMenuOpacityStateChanged(evt);
            }
        });

        lblBlackColour.setText("Text");

        txtBlackColour.setEditable(false);
        txtBlackColour.setText("#00FF00");
        txtBlackColour.setPreferredSize(new java.awt.Dimension(70, 24));

        pnlBlackColourPreviewContainer.setLayout(new javax.swing.BoxLayout(pnlBlackColourPreviewContainer, javax.swing.BoxLayout.Y_AXIS));
        pnlBlackColourPreviewContainer.add(glueBlackColoura);

        pnlBlackColourPreview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlBlackColourPreview.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout pnlBlackColourPreviewLayout = new javax.swing.GroupLayout(pnlBlackColourPreview);
        pnlBlackColourPreview.setLayout(pnlBlackColourPreviewLayout);
        pnlBlackColourPreviewLayout.setHorizontalGroup(
            pnlBlackColourPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlBlackColourPreviewLayout.setVerticalGroup(
            pnlBlackColourPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlBlackColourPreviewContainer.add(pnlBlackColourPreview);
        pnlBlackColourPreviewContainer.add(glueBlackColourb);

        btnBlackColourChange.setText("Change");
        btnBlackColourChange.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnBlackColourChangeActionPerformed(evt);
            }
        });

        lblWhiteColour.setText("Background");
        lblWhiteColour.setToolTipText("");

        txtWhiteColour.setEditable(false);
        txtWhiteColour.setText("#00FF00");
        txtWhiteColour.setPreferredSize(new java.awt.Dimension(70, 24));

        pnlWhiteColourPreviewContainer.setLayout(new javax.swing.BoxLayout(pnlWhiteColourPreviewContainer, javax.swing.BoxLayout.Y_AXIS));
        pnlWhiteColourPreviewContainer.add(glueWhiteColoura);

        pnlWhiteColourPreview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlWhiteColourPreview.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout pnlWhiteColourPreviewLayout = new javax.swing.GroupLayout(pnlWhiteColourPreview);
        pnlWhiteColourPreview.setLayout(pnlWhiteColourPreviewLayout);
        pnlWhiteColourPreviewLayout.setHorizontalGroup(
            pnlWhiteColourPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlWhiteColourPreviewLayout.setVerticalGroup(
            pnlWhiteColourPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlWhiteColourPreviewContainer.add(pnlWhiteColourPreview);
        pnlWhiteColourPreviewContainer.add(glueWhiteColourb);

        btnWhiteColourChange.setText("Change");
        btnWhiteColourChange.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnWhiteColourChangeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlThemeLayout = new javax.swing.GroupLayout(pnlTheme);
        pnlTheme.setLayout(pnlThemeLayout);
        pnlThemeLayout.setHorizontalGroup(
            pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThemeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlThemeLayout.createSequentialGroup()
                        .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pnlThemeButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pnlThemeLayout.createSequentialGroup()
                                .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblPrimaryColour1)
                                    .addComponent(lblPrimaryColour2)
                                    .addComponent(lblPrimaryColour3)
                                    .addComponent(lblSecondaryColour1)
                                    .addComponent(lblSecondaryColour2)
                                    .addComponent(lblSecondaryColour3)
                                    .addComponent(lblBlackColour)
                                    .addComponent(lblWhiteColour))
                                .addGap(18, 18, 18)
                                .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlThemeLayout.createSequentialGroup()
                                        .addComponent(txtPrimaryColour1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(pnlPrimaryColour1PreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnPrimaryColour1Change))
                                    .addGroup(pnlThemeLayout.createSequentialGroup()
                                        .addComponent(txtPrimaryColour2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(pnlPrimaryColour2PreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnPrimaryColour2Change))
                                    .addGroup(pnlThemeLayout.createSequentialGroup()
                                        .addComponent(txtPrimaryColour3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(pnlPrimaryColour3PreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnPrimaryColour3Change))
                                    .addGroup(pnlThemeLayout.createSequentialGroup()
                                        .addComponent(txtSecondaryColour1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(pnlSecondaryColour1PreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnSecondaryColour1Change))
                                    .addGroup(pnlThemeLayout.createSequentialGroup()
                                        .addComponent(txtSecondaryColour2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(pnlSecondaryColour2PreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnSecondaryColour2Change))
                                    .addGroup(pnlThemeLayout.createSequentialGroup()
                                        .addComponent(txtSecondaryColour3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(pnlSecondaryColour3PreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnSecondaryColour3Change))
                                    .addGroup(pnlThemeLayout.createSequentialGroup()
                                        .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlThemeLayout.createSequentialGroup()
                                                .addComponent(txtWhiteColour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(pnlWhiteColourPreviewContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(pnlThemeLayout.createSequentialGroup()
                                                .addComponent(txtBlackColour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(pnlBlackColourPreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btnBlackColourChange)
                                            .addComponent(btnWhiteColourChange, javax.swing.GroupLayout.Alignment.TRAILING))))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(pnlThemeLayout.createSequentialGroup()
                        .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMenuOpacity)
                            .addGroup(pnlThemeLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(sldMenuOpacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        pnlThemeLayout.setVerticalGroup(
            pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlThemeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPrimaryColour1)
                        .addComponent(txtPrimaryColour1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPrimaryColour1Change))
                    .addComponent(pnlPrimaryColour1PreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPrimaryColour2)
                        .addComponent(txtPrimaryColour2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPrimaryColour2Change))
                    .addComponent(pnlPrimaryColour2PreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPrimaryColour3)
                        .addComponent(txtPrimaryColour3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPrimaryColour3Change))
                    .addComponent(pnlPrimaryColour3PreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblSecondaryColour1)
                        .addComponent(txtSecondaryColour1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSecondaryColour1Change))
                    .addComponent(pnlSecondaryColour1PreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblSecondaryColour2)
                        .addComponent(txtSecondaryColour2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSecondaryColour2Change))
                    .addComponent(pnlSecondaryColour2PreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblSecondaryColour3)
                        .addComponent(txtSecondaryColour3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSecondaryColour3Change))
                    .addComponent(pnlSecondaryColour3PreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblBlackColour)
                        .addComponent(txtBlackColour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBlackColourChange))
                    .addComponent(pnlBlackColourPreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlThemeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblWhiteColour)
                        .addComponent(txtWhiteColour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnWhiteColourChange))
                    .addComponent(pnlWhiteColourPreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblMenuOpacity)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sldMenuOpacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlThemeButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlTabs.addTab("Theme", pnlTheme);

        chkWindowModeEnabled.setText("Enable Windowed Mode");
        chkWindowModeEnabled.addItemListener(new java.awt.event.ItemListener()
        {
            public void itemStateChanged(java.awt.event.ItemEvent evt)
            {
                chkWindowModeEnabledItemStateChanged(evt);
            }
        });

        lblDimensions.setText("Dimensions");

        lblDimensionsX.setText("x");

        lblBackground.setText("Background");

        txtBackgroundColour.setEditable(false);
        txtBackgroundColour.setText("#00FF00");
        txtBackgroundColour.setPreferredSize(new java.awt.Dimension(70, 24));

        btnBackgroundColourChange.setText("Change");
        btnBackgroundColourChange.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnBackgroundColourChangeActionPerformed(evt);
            }
        });

        spnWindowWidth.setModel(new javax.swing.SpinnerNumberModel(0, 0, 10000, 1));
        spnWindowWidth.setPreferredSize(new java.awt.Dimension(60, 24));
        spnWindowWidth.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                spnWindowWidthStateChanged(evt);
            }
        });

        spnWindowHeight.setModel(new javax.swing.SpinnerNumberModel(0, 0, 10000, 1));
        spnWindowHeight.setMinimumSize(new java.awt.Dimension(30, 20));
        spnWindowHeight.setPreferredSize(new java.awt.Dimension(60, 24));
        spnWindowHeight.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                spnWindowHeightStateChanged(evt);
            }
        });

        lblBackgroundColour.setText("Colour");

        pnlBackgroundPreviewContainer.setLayout(new javax.swing.BoxLayout(pnlBackgroundPreviewContainer, javax.swing.BoxLayout.Y_AXIS));
        pnlBackgroundPreviewContainer.add(glueBackground);

        pnlBackgroundPreview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlBackgroundPreview.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout pnlBackgroundPreviewLayout = new javax.swing.GroupLayout(pnlBackgroundPreview);
        pnlBackgroundPreview.setLayout(pnlBackgroundPreviewLayout);
        pnlBackgroundPreviewLayout.setHorizontalGroup(
            pnlBackgroundPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlBackgroundPreviewLayout.setVerticalGroup(
            pnlBackgroundPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlBackgroundPreviewContainer.add(pnlBackgroundPreview);
        pnlBackgroundPreviewContainer.add(glueBackground2);

        lblBackgroundImageCaption.setText("Image");

        pnlBackgroundImage.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlBackgroundImage.setPreferredSize(new java.awt.Dimension(96, 96));
        pnlBackgroundImage.setLayout(new java.awt.BorderLayout());

        lblBackgroundImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlBackgroundImage.add(lblBackgroundImage, java.awt.BorderLayout.CENTER);

        btnBackgroundImageChange.setText("Change");
        btnBackgroundImageChange.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnBackgroundImageChangeActionPerformed(evt);
            }
        });

        btnBackgroundImageRemove.setText("Remove");
        btnBackgroundImageRemove.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnBackgroundImageRemoveActionPerformed(evt);
            }
        });

        cmbBackgroundImageMode.setModel(new javax.swing.DefaultComboBoxModel<String>());
        cmbBackgroundImageMode.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cmbBackgroundImageModeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlWindowModeLayout = new javax.swing.GroupLayout(pnlWindowMode);
        pnlWindowMode.setLayout(pnlWindowModeLayout);
        pnlWindowModeLayout.setHorizontalGroup(
            pnlWindowModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWindowModeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlWindowModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkWindowModeEnabled)
                    .addGroup(pnlWindowModeLayout.createSequentialGroup()
                        .addGroup(pnlWindowModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDimensions)
                            .addComponent(lblBackground)
                            .addGroup(pnlWindowModeLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(pnlWindowModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblBackgroundImageCaption)
                                    .addComponent(lblBackgroundColour))))
                        .addGap(18, 18, 18)
                        .addGroup(pnlWindowModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(pnlWindowModeLayout.createSequentialGroup()
                                .addComponent(spnWindowWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblDimensionsX)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnWindowHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlWindowModeLayout.createSequentialGroup()
                                .addComponent(txtBackgroundColour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlBackgroundPreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnBackgroundColourChange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(pnlWindowModeLayout.createSequentialGroup()
                                .addComponent(pnlBackgroundImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlWindowModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnBackgroundImageRemove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnBackgroundImageChange, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cmbBackgroundImageMode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap(139, Short.MAX_VALUE))
        );
        pnlWindowModeLayout.setVerticalGroup(
            pnlWindowModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWindowModeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkWindowModeEnabled)
                .addGap(18, 18, 18)
                .addGroup(pnlWindowModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDimensions)
                    .addComponent(lblDimensionsX)
                    .addComponent(spnWindowWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnWindowHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblBackground)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlWindowModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlWindowModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblBackgroundColour)
                        .addComponent(txtBackgroundColour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBackgroundColourChange))
                    .addComponent(pnlBackgroundPreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlWindowModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlWindowModeLayout.createSequentialGroup()
                        .addComponent(btnBackgroundImageChange)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbBackgroundImageMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBackgroundImageRemove))
                    .addGroup(pnlWindowModeLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(lblBackgroundImageCaption))
                    .addComponent(pnlBackgroundImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(150, Short.MAX_VALUE))
        );

        pnlTabs.addTab("WindowMode", pnlWindowMode);

        pnlAbout.setLayout(new javax.swing.BoxLayout(pnlAbout, javax.swing.BoxLayout.Y_AXIS));
        pnlAbout.add(glue1);

        lblIcon.setAlignmentX(0.5F);
        lblIcon.setMinimumSize(new java.awt.Dimension(64, 64));
        lblIcon.setPreferredSize(new java.awt.Dimension(64, 64));
        pnlAbout.add(lblIcon);
        pnlAbout.add(rigid1);

        lblShimejiEE.setFont(lblShimejiEE.getFont().deriveFont(lblShimejiEE.getFont().getStyle() | java.awt.Font.BOLD, lblShimejiEE.getFont().getSize()+10));
        lblShimejiEE.setText("Shimeji");
        lblShimejiEE.setAlignmentX(0.5F);
        pnlAbout.add(lblShimejiEE);
        pnlAbout.add(rigid2);

        lblVersion.setFont(lblVersion.getFont().deriveFont(lblVersion.getFont().getSize()+4f));
        lblVersion.setText("1.0.21.3");
        lblVersion.setAlignmentX(0.5F);
        pnlAbout.add(lblVersion);
        pnlAbout.add(rigid3);

        lblDevelopedBy.setText("developed by");
        lblDevelopedBy.setAlignmentX(0.5F);
        pnlAbout.add(lblDevelopedBy);

        lblKilkakon.setText("Kilkakon");
        lblKilkakon.setAlignmentX(0.5F);
        pnlAbout.add(lblKilkakon);
        pnlAbout.add(rigid4);

        pnlAboutButtons.setMaximumSize(new java.awt.Dimension(32767, 36));
        pnlAboutButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        btnWebsite.setText("Website");
        btnWebsite.setAlignmentX(0.5F);
        btnWebsite.setMaximumSize(new java.awt.Dimension(130, 26));
        btnWebsite.setPreferredSize(new java.awt.Dimension(100, 26));
        btnWebsite.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnWebsiteActionPerformed(evt);
            }
        });
        pnlAboutButtons.add(btnWebsite);

        btnDiscord.setText("Discord");
        btnDiscord.setAlignmentX(0.5F);
        btnDiscord.setMaximumSize(new java.awt.Dimension(130, 26));
        btnDiscord.setPreferredSize(new java.awt.Dimension(100, 26));
        btnDiscord.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnDiscordActionPerformed(evt);
            }
        });
        pnlAboutButtons.add(btnDiscord);

        btnPatreon.setText("Patreon");
        btnPatreon.setAlignmentX(0.5F);
        btnPatreon.setMaximumSize(new java.awt.Dimension(130, 26));
        btnPatreon.setPreferredSize(new java.awt.Dimension(100, 26));
        btnPatreon.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnPatreonActionPerformed(evt);
            }
        });
        pnlAboutButtons.add(btnPatreon);

        pnlAbout.add(pnlAboutButtons);
        pnlAbout.add(glue2);

        pnlTabs.addTab("About", pnlAbout);

        pnlFooter.setPreferredSize(new java.awt.Dimension(380, 36));
        pnlFooter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        btnDone.setText("Done");
        btnDone.setMaximumSize(new java.awt.Dimension(130, 26));
        btnDone.setMinimumSize(new java.awt.Dimension(95, 23));
        btnDone.setName(""); // NOI18N
        btnDone.setPreferredSize(new java.awt.Dimension(130, 26));
        btnDone.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnDoneActionPerformed(evt);
            }
        });
        pnlFooter.add(btnDone);

        btnCancel.setText("Cancel");
        btnCancel.setMaximumSize(new java.awt.Dimension(130, 26));
        btnCancel.setMinimumSize(new java.awt.Dimension(95, 23));
        btnCancel.setPreferredSize(new java.awt.Dimension(130, 26));
        btnCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnCancelActionPerformed(evt);
            }
        });
        pnlFooter.add(btnCancel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlFooter, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(pnlTabs))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlTabs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDoneActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnDoneActionPerformed
    {//GEN-HEADEREND:event_btnDoneActionPerformed
        // done button
        try
        {
            Properties properties = Main.getInstance( ).getProperties( );
            String interactiveWindows = listData.toString( ).replace( "[", "" ).replace( "]", "" ).replace( ", ", "/" );
            String[ ] windowArray = properties.getProperty( "WindowSize", "600x500" ).split( "x" );
            Dimension window = new Dimension( Integer.parseInt( windowArray[ 0 ] ), Integer.parseInt( windowArray[ 1 ] ) );
            float menuScaling = Float.parseFloat( properties.getProperty( "MenuDPI", "96" ) ) / 96;

            environmentReloadRequired = properties.getProperty( "Environment", "generic" ).equals( "virtual" ) != windowedMode ||
                                        !window.equals( windowSize ) ||
                                        !Color.decode( properties.getProperty( "Background", "#00FF00" ) ).equals( backgroundColour ) ||
                                        !properties.getProperty( "BackgroundMode", "centre" ).equals( backgroundMode ) ||
                                        !properties.getProperty( "BackgroundImage", "" ).equalsIgnoreCase( backgroundImage == null ? "" : backgroundImage );
            imageReloadRequired = !properties.getProperty( "Filter", "false" ).equalsIgnoreCase( filter ) || 
                                  Double.parseDouble( properties.getProperty( "Scaling", "1.0" ) ) != scaling || 
                                  Double.parseDouble( properties.getProperty( "Opacity", "1.0" ) ) != opacity;
            interactiveWindowReloadRequired = !properties.getProperty( "InteractiveWindows", "" ).equals( interactiveWindows );
            FileOutputStream output = new FileOutputStream( configFile );
            
            try
            {
                properties.setProperty( "AlwaysShowShimejiChooser", alwaysShowShimejiChooser.toString( ) );
                properties.setProperty( "AlwaysShowInformationScreen", alwaysShowInformationScreen.toString( ) );
                properties.setProperty( "Opacity", Double.toString( opacity ) );
                properties.setProperty( "Scaling", Double.toString( scaling ) );
                properties.setProperty( "Filter", filter );
                properties.setProperty( "InteractiveWindows", interactiveWindows );
                properties.setProperty( "Environment", windowedMode ? "virtual" : "generic" );
                if( windowedMode )
                {
                    properties.setProperty( "WindowSize", windowSize.width + "x" + windowSize.height );
                    properties.setProperty( "Background", String.format( "#%02X%02X%02X", backgroundColour.getRed( ), backgroundColour.getGreen( ), backgroundColour.getBlue( ) ) );
                    properties.setProperty( "BackgroundMode", backgroundMode );
                    properties.setProperty( "BackgroundImage", backgroundImage == null ? "" : backgroundImage );
                }
                
                properties.store( output, "Shimeji-ee Configuration Options" );
            }
            finally
            {
                output.close( );
            }
            
            FileOutputStream themeOutput = new FileOutputStream( themeFile );
            try
            {
                properties = new Properties( );
                properties.setProperty( "nimrodlf.p1", String.format( "#%02X%02X%02X", primaryColour1.getRed( ), primaryColour1.getGreen( ), primaryColour1.getBlue( ) ) );
                properties.setProperty( "nimrodlf.p2", String.format( "#%02X%02X%02X", primaryColour2.getRed( ), primaryColour2.getGreen( ), primaryColour2.getBlue( ) ) );
                properties.setProperty( "nimrodlf.p3", String.format( "#%02X%02X%02X", primaryColour3.getRed( ), primaryColour3.getGreen( ), primaryColour3.getBlue( ) ) );
                properties.setProperty( "nimrodlf.s1", String.format( "#%02X%02X%02X", secondaryColour1.getRed( ), secondaryColour1.getGreen( ), secondaryColour1.getBlue( ) ) );
                properties.setProperty( "nimrodlf.s2", String.format( "#%02X%02X%02X", secondaryColour2.getRed( ), secondaryColour2.getGreen( ), secondaryColour2.getBlue( ) ) );
                properties.setProperty( "nimrodlf.s3", String.format( "#%02X%02X%02X", secondaryColour3.getRed( ), secondaryColour3.getGreen( ), secondaryColour3.getBlue( ) ) );
                properties.setProperty( "nimrodlf.b", String.format( "#%02X%02X%02X", blackColour.getRed( ), blackColour.getGreen( ), blackColour.getBlue( ) ) );
                properties.setProperty( "nimrodlf.w", String.format( "#%02X%02X%02X", whiteColour.getRed( ), whiteColour.getGreen( ), whiteColour.getBlue( ) ) );
                properties.setProperty( "nimrodlf.menuOpacity", String.valueOf( (int)( menuOpacity * 255 ) ) );
                properties.setProperty( "nimrodlf.frameOpacity", "255" );
                properties.setProperty( "nimrodlf.font", String.format( "%s-%s-%d", 
                                                                        font.getName( ),
                                                                        font.getStyle( ) == Font.PLAIN ? "PLAIN" : 
                                                                        font.getStyle( ) == Font.BOLD ? "BOLD" :
                                                                        font.getStyle( ) == Font.ITALIC ? "ITALIC" :
                                                                        "BOLDITALIC",
                                                                        (int)( font.getSize( ) / menuScaling ) ) );
                properties.store( themeOutput, null );
            }
            finally
            {
                themeOutput.close( );
            }
        }
        catch( Exception e )
        {
        }
        dispose( );
    }//GEN-LAST:event_btnDoneActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnCancelActionPerformed
    {//GEN-HEADEREND:event_btnCancelActionPerformed
        theme = oldTheme;
        refreshTheme( );
        dispose( );
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnAddInteractiveWindowActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnAddInteractiveWindowActionPerformed
    {//GEN-HEADEREND:event_btnAddInteractiveWindowActionPerformed
        // add button
        String inputValue = JOptionPane.showInputDialog( rootPane, Main.getInstance( ).getLanguageBundle( ).getString( "InteractiveWindowHintMessage" ), Main.getInstance( ).getLanguageBundle( ).getString( "AddInteractiveWindow" ), JOptionPane.QUESTION_MESSAGE ).trim( );
        if( !inputValue.isEmpty( ) && !inputValue.contains( "/" ) )
        {
            listData.add( inputValue );
            lstInteractiveWindows.setListData( listData.toArray( ) );
        }
    }//GEN-LAST:event_btnAddInteractiveWindowActionPerformed

    private void btnRemoveInteractiveWindowActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnRemoveInteractiveWindowActionPerformed
    {//GEN-HEADEREND:event_btnRemoveInteractiveWindowActionPerformed
        // delete button
        if( lstInteractiveWindows.getSelectedIndex( ) != -1 )
        {
            listData.remove( lstInteractiveWindows.getSelectedIndex( ) );
            lstInteractiveWindows.setListData( listData.toArray( ) );
        }
    }//GEN-LAST:event_btnRemoveInteractiveWindowActionPerformed

    private void chkAlwaysShowShimejiChooserItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_chkAlwaysShowShimejiChooserItemStateChanged
    {//GEN-HEADEREND:event_chkAlwaysShowShimejiChooserItemStateChanged
        alwaysShowShimejiChooser = evt.getStateChange( ) == ItemEvent.SELECTED;
    }//GEN-LAST:event_chkAlwaysShowShimejiChooserItemStateChanged

    private void radFilterItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_radFilterItemStateChanged
    {//GEN-HEADEREND:event_radFilterItemStateChanged
        if( evt.getStateChange( ) == ItemEvent.SELECTED )
        {
            Object source = evt.getItemSelectable( );
            
            if( source == radFilterNearest )
                filter = "nearest";
            else if( source == radFilterHqx )
                filter = "hqx";
            else
                filter = "bicubic";
        }
    }//GEN-LAST:event_radFilterItemStateChanged

    private void sldScalingStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_sldScalingStateChanged
    {//GEN-HEADEREND:event_sldScalingStateChanged
        if( !sldScaling.getValueIsAdjusting( ) )
        {
            if( sldScaling.getValue( ) == 0 )
                sldScaling.setValue( 5 );
            else
            {
                scaling = sldScaling.getValue( ) / 10.0;
                if( scaling == 2 || scaling == 3 || scaling == 4 || scaling == 6 || scaling == 8 )
                {
                    radFilterHqx.setEnabled( true );
                }
                else
                {
                    radFilterHqx.setEnabled( false );
                    if( filter.equals( "hqx" ) )
                    {
                        radFilterNearest.setSelected( true );
                    }
                }
            }
        }
    }//GEN-LAST:event_sldScalingStateChanged

    private void btnWebsiteActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnWebsiteActionPerformed
    {//GEN-HEADEREND:event_btnWebsiteActionPerformed
        browseToUrl( "http://kilkakon.com/" );
    }//GEN-LAST:event_btnWebsiteActionPerformed

    private void btnDiscordActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnDiscordActionPerformed
    {//GEN-HEADEREND:event_btnDiscordActionPerformed
        browseToUrl( "https://discord.gg/NBq3zqfA2B" );
    }//GEN-LAST:event_btnDiscordActionPerformed

    private void btnPatreonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPatreonActionPerformed
    {//GEN-HEADEREND:event_btnPatreonActionPerformed
        browseToUrl( "https://patreon.com/kilkakon" );
    }//GEN-LAST:event_btnPatreonActionPerformed

    private void sldOpacityStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_sldOpacityStateChanged
    {//GEN-HEADEREND:event_sldOpacityStateChanged
        if( !sldOpacity.getValueIsAdjusting( ) )
        {
            if( sldOpacity.getValue( ) == 0 )
                sldOpacity.setValue( 5 );
            else
                opacity = sldOpacity.getValue( ) / 100.0;
        }
    }//GEN-LAST:event_sldOpacityStateChanged

    private void spnWindowHeightStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spnWindowHeightStateChanged
    {//GEN-HEADEREND:event_spnWindowHeightStateChanged
        windowSize.height = ( (SpinnerNumberModel)spnWindowHeight.getModel( ) ).getNumber( ).intValue( );
    }//GEN-LAST:event_spnWindowHeightStateChanged

    private void spnWindowWidthStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spnWindowWidthStateChanged
    {//GEN-HEADEREND:event_spnWindowWidthStateChanged
        windowSize.width = ( (SpinnerNumberModel)spnWindowWidth.getModel( ) ).getNumber( ).intValue( );
    }//GEN-LAST:event_spnWindowWidthStateChanged

    private void btnBackgroundColourChangeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnBackgroundColourChangeActionPerformed
    {//GEN-HEADEREND:event_btnBackgroundColourChangeActionPerformed
        backgroundColour = chooseColour( backgroundColour, txtBackgroundColour, pnlBackgroundPreview, "ChooseBackgroundColour" );
    }//GEN-LAST:event_btnBackgroundColourChangeActionPerformed

    private void chkWindowModeEnabledItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_chkWindowModeEnabledItemStateChanged
    {//GEN-HEADEREND:event_chkWindowModeEnabledItemStateChanged
        windowedMode = evt.getStateChange( ) == ItemEvent.SELECTED;
        spnWindowWidth.setEnabled( windowedMode );
        spnWindowHeight.setEnabled( windowedMode );
        btnBackgroundColourChange.setEnabled( windowedMode );
        btnBackgroundImageChange.setEnabled( windowedMode );
        cmbBackgroundImageMode.setEnabled( windowedMode && backgroundImage != null );
        btnBackgroundImageRemove.setEnabled( windowedMode && backgroundImage != null );
    }//GEN-LAST:event_chkWindowModeEnabledItemStateChanged

    private void btnBackgroundImageChangeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnBackgroundImageChangeActionPerformed
    {//GEN-HEADEREND:event_btnBackgroundImageChangeActionPerformed
        final JFileChooser dialog = new JFileChooser( );
        dialog.setDialogTitle( Main.getInstance( ).getLanguageBundle( ).getString( "ChooseBackgroundImage" ) );
        //dialog.setFileFilter(  );
                
        if( dialog.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION )
        {
            try
            {
                backgroundImage = dialog.getSelectedFile( ).getCanonicalPath( );
                refreshBackgroundImage( );
            }
            catch( Exception e )
            {
                backgroundImage = null;
                lblBackgroundImage.setIcon( null );
            }
            cmbBackgroundImageMode.setEnabled( windowedMode && backgroundImage != null );
            btnBackgroundImageRemove.setEnabled( windowedMode && backgroundImage != null );
        }
    }//GEN-LAST:event_btnBackgroundImageChangeActionPerformed

    private void btnBackgroundImageRemoveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnBackgroundImageRemoveActionPerformed
    {//GEN-HEADEREND:event_btnBackgroundImageRemoveActionPerformed
        backgroundImage = null;
        lblBackgroundImage.setIcon( null );
        cmbBackgroundImageMode.setEnabled( false );
        btnBackgroundImageRemove.setEnabled( false );
    }//GEN-LAST:event_btnBackgroundImageRemoveActionPerformed

    private void cmbBackgroundImageModeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cmbBackgroundImageModeActionPerformed
    {//GEN-HEADEREND:event_cmbBackgroundImageModeActionPerformed
        if( cmbBackgroundImageMode.getSelectedIndex( ) > -1 )
            backgroundMode = backgroundModes[ cmbBackgroundImageMode.getSelectedIndex( ) ];
        refreshBackgroundImage( );
    }//GEN-LAST:event_cmbBackgroundImageModeActionPerformed

    private void btnPrimaryColour1ChangeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPrimaryColour1ChangeActionPerformed
    {//GEN-HEADEREND:event_btnPrimaryColour1ChangeActionPerformed
        primaryColour1 = chooseColour( primaryColour1, txtPrimaryColour1, pnlPrimaryColour1Preview, "ChooseColour" );
        theme.setPrimary1( primaryColour1 );
        refreshTheme( );
    }//GEN-LAST:event_btnPrimaryColour1ChangeActionPerformed

    private void btnPrimaryColour2ChangeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPrimaryColour2ChangeActionPerformed
    {//GEN-HEADEREND:event_btnPrimaryColour2ChangeActionPerformed
        primaryColour2 = chooseColour( primaryColour2, txtPrimaryColour2, pnlPrimaryColour2Preview, "ChooseColour" );
        theme.setPrimary2( primaryColour2 );
        refreshTheme( );
    }//GEN-LAST:event_btnPrimaryColour2ChangeActionPerformed

    private void btnPrimaryColour3ChangeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPrimaryColour3ChangeActionPerformed
    {//GEN-HEADEREND:event_btnPrimaryColour3ChangeActionPerformed
        primaryColour3 = chooseColour( primaryColour3, txtPrimaryColour3, pnlPrimaryColour3Preview, "ChooseColour" );
        theme.setPrimary3( primaryColour3 );
        refreshTheme( );
    }//GEN-LAST:event_btnPrimaryColour3ChangeActionPerformed

    private void btnSecondaryColour1ChangeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnSecondaryColour1ChangeActionPerformed
    {//GEN-HEADEREND:event_btnSecondaryColour1ChangeActionPerformed
        secondaryColour1 = chooseColour( secondaryColour1, txtSecondaryColour1, pnlSecondaryColour1Preview, "ChooseColour" );
        theme.setSecondary1( secondaryColour1 );
        refreshTheme( );
    }//GEN-LAST:event_btnSecondaryColour1ChangeActionPerformed

    private void btnSecondaryColour2ChangeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnSecondaryColour2ChangeActionPerformed
    {//GEN-HEADEREND:event_btnSecondaryColour2ChangeActionPerformed
        secondaryColour2 = chooseColour( secondaryColour2, txtSecondaryColour2, pnlSecondaryColour2Preview, "ChooseColour" );
        theme.setSecondary2( secondaryColour2 );
        refreshTheme( );
    }//GEN-LAST:event_btnSecondaryColour2ChangeActionPerformed

    private void btnSecondaryColour3ChangeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnSecondaryColour3ChangeActionPerformed
    {//GEN-HEADEREND:event_btnSecondaryColour3ChangeActionPerformed
        secondaryColour3 = chooseColour( secondaryColour3, txtSecondaryColour3, pnlSecondaryColour3Preview, "ChooseColour" );
        theme.setSecondary3( secondaryColour3 );
        refreshTheme( );
    }//GEN-LAST:event_btnSecondaryColour3ChangeActionPerformed

    private void sldMenuOpacityStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_sldMenuOpacityStateChanged
    {//GEN-HEADEREND:event_sldMenuOpacityStateChanged
        if( !sldMenuOpacity.getValueIsAdjusting( ) )
        {
            if( sldMenuOpacity.getValue( ) == 0 )
                sldMenuOpacity.setValue( 1 );
            else
            {
                menuOpacity = sldMenuOpacity.getValue( ) / 100.0;
                theme.setMenuOpacity( (int)( menuOpacity * 255 ) );
            }
        }
    }//GEN-LAST:event_sldMenuOpacityStateChanged

    private void btnChangeFontActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnChangeFontActionPerformed
    {//GEN-HEADEREND:event_btnChangeFontActionPerformed
        JFrame frame = (JFrame)SwingUtilities.getWindowAncestor( this );
        NimRODFontDialog dialog = new NimRODFontDialog( frame, font );
        dialog.pack( );
        dialog.setLocationRelativeTo( frame );
        dialog.setVisible( true );
        font = dialog.getSelectedFont( );
        if( !dialog.isCanceled( ) )
        {
            theme.setFont( font );
            refreshTheme( );
        }
    }//GEN-LAST:event_btnChangeFontActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnResetActionPerformed
    {//GEN-HEADEREND:event_btnResetActionPerformed
        float menuScaling = Float.parseFloat( Main.getInstance( ).getProperties( ).getProperty( "MenuDPI", "96" ) ) / 96;
        primaryColour1 = Color.decode( "#1EA6EB" );
        primaryColour2 = Color.decode( "#28B0F5" );
        primaryColour3 = Color.decode( "#32BAFF" );
        secondaryColour1 = Color.decode( "#BCBCBE" );
        secondaryColour2 = Color.decode( "#C6C6C8" );
        secondaryColour3 = Color.decode( "#D0D0D2" );
        blackColour = Color.decode( "#000000" );
        whiteColour = Color.decode( "#FFFFFF" );
        font = Font.decode( "SansSerif-PLAIN-12" );
        font = font.deriveFont( font.getSize( ) * menuScaling );
        pnlPrimaryColour1Preview.setBackground( primaryColour1 );
        txtPrimaryColour1.setText( String.format( "#%02X%02X%02X", primaryColour1.getRed( ), primaryColour1.getGreen( ), primaryColour1.getBlue( ) ) );
        pnlPrimaryColour2Preview.setBackground( primaryColour2 );
        txtPrimaryColour2.setText( String.format( "#%02X%02X%02X", primaryColour2.getRed( ), primaryColour2.getGreen( ), primaryColour2.getBlue( ) ) );
        pnlPrimaryColour3Preview.setBackground( primaryColour3 );
        txtPrimaryColour3.setText( String.format( "#%02X%02X%02X", primaryColour3.getRed( ), primaryColour2.getGreen( ), primaryColour3.getBlue( ) ) );
        pnlSecondaryColour1Preview.setBackground( secondaryColour1 );
        txtSecondaryColour1.setText( String.format( "#%02X%02X%02X", secondaryColour1.getRed( ), secondaryColour1.getGreen( ), secondaryColour1.getBlue( ) ) );
        pnlSecondaryColour2Preview.setBackground( secondaryColour2 );
        txtSecondaryColour2.setText( String.format( "#%02X%02X%02X", secondaryColour2.getRed( ), secondaryColour2.getGreen( ), secondaryColour2.getBlue( ) ) );
        pnlSecondaryColour3Preview.setBackground( secondaryColour3 );
        txtSecondaryColour3.setText( String.format( "#%02X%02X%02X", secondaryColour3.getRed( ), secondaryColour3.getGreen( ), secondaryColour3.getBlue( ) ) );
        pnlBlackColourPreview.setBackground( blackColour );
        txtBlackColour.setText( String.format( "#%02X%02X%02X", blackColour.getRed( ), blackColour.getGreen( ), blackColour.getBlue( ) ) );
        pnlWhiteColourPreview.setBackground( whiteColour );
        txtWhiteColour.setText( String.format( "#%02X%02X%02X", whiteColour.getRed( ), whiteColour.getGreen( ), whiteColour.getBlue( ) ) );
        menuOpacity = 1.0;
        theme.setPrimary1( primaryColour1 );
        theme.setPrimary2( primaryColour2 );
        theme.setPrimary3( primaryColour3 );
        theme.setSecondary1( secondaryColour1 );
        theme.setSecondary2( secondaryColour2 );
        theme.setSecondary3( secondaryColour3 );
        theme.setBlack( blackColour );
        theme.setWhite( whiteColour );
        theme.setFont( font );
        sldMenuOpacity.setValue( (int)( menuOpacity * 100 ) );
        refreshTheme( );
    }//GEN-LAST:event_btnResetActionPerformed

    private void chkAlwaysShowInformationScreenItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_chkAlwaysShowInformationScreenItemStateChanged
    {//GEN-HEADEREND:event_chkAlwaysShowInformationScreenItemStateChanged
        alwaysShowInformationScreen = evt.getStateChange( ) == ItemEvent.SELECTED;
    }//GEN-LAST:event_chkAlwaysShowInformationScreenItemStateChanged

    private void btnBlackColourChangeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnBlackColourChangeActionPerformed
    {//GEN-HEADEREND:event_btnBlackColourChangeActionPerformed
        blackColour = chooseColour( blackColour, txtBlackColour, pnlBlackColourPreview, "ChooseColour" );
        theme.setBlack( blackColour );
        refreshTheme( );
    }//GEN-LAST:event_btnBlackColourChangeActionPerformed

    private void btnWhiteColourChangeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnWhiteColourChangeActionPerformed
    {//GEN-HEADEREND:event_btnWhiteColourChangeActionPerformed
        whiteColour = chooseColour( whiteColour, txtWhiteColour, pnlWhiteColourPreview, "ChooseColour" );
        theme.setWhite( whiteColour );
        refreshTheme( );
    }//GEN-LAST:event_btnWhiteColourChangeActionPerformed

    private Color chooseColour( Color colour, JTextField field, JPanel preview, String title )
    {
        Color newColour = JColorChooser.showDialog( this, Main.getInstance( ).getLanguageBundle( ).getString( title ), colour );

        if( newColour != null )
        {
            colour = newColour;
            field.setText( String.format( "#%02X%02X%02X", colour.getRed( ), colour.getGreen( ), colour.getBlue( ) ) );
            preview.setBackground( colour );
        }
        
        return colour;
    }
    
    private void refreshBackgroundImage( )
    {
        Dimension size = pnlBackgroundImage.getPreferredSize( );
        Image image = new ImageIcon( backgroundImage ).getImage( );
        
        if( backgroundMode.equals( "stretch" ) )
        {
            image = image.getScaledInstance( size.width, 
                                             size.height,
                                             java.awt.Image.SCALE_SMOOTH );
            
        }
        else if( !backgroundMode.equals( "centre" ) )
        {
            double factor = backgroundMode.equals( "fit" ) ?
                            Math.min( size.width / (double)image.getWidth( null ), size.height / (double)image.getHeight( null ) ) :
                            Math.max( size.width / (double)image.getWidth( null ), size.height / (double)image.getHeight( null ) );
            image = image.getScaledInstance( (int)( factor * image.getWidth( null ) ), 
                                             (int)( factor * image.getHeight( null ) ),
                                             java.awt.Image.SCALE_SMOOTH );
        }
        
        lblBackgroundImage.setIcon( new ImageIcon( image ) );
        lblBackgroundImage.setPreferredSize( new Dimension( image.getWidth( null ), image.getHeight( null ) ) );
    }
    
    private void refreshTheme( )
    {
        try
        {
            NimRODLookAndFeel.setCurrentTheme( theme );
            UIManager.setLookAndFeel( lookAndFeel );
            SwingUtilities.updateComponentTreeUI( this );
            pack( );
        }
        catch( UnsupportedLookAndFeelException ex )
        {
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddInteractiveWindow;
    private javax.swing.JButton btnBackgroundColourChange;
    private javax.swing.JButton btnBackgroundImageChange;
    private javax.swing.JButton btnBackgroundImageRemove;
    private javax.swing.JButton btnBlackColourChange;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnChangeFont;
    private javax.swing.JButton btnDiscord;
    private javax.swing.JButton btnDone;
    private javax.swing.JButton btnPatreon;
    private javax.swing.JButton btnPrimaryColour1Change;
    private javax.swing.JButton btnPrimaryColour2Change;
    private javax.swing.JButton btnPrimaryColour3Change;
    private javax.swing.JButton btnRemoveInteractiveWindow;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSecondaryColour1Change;
    private javax.swing.JButton btnSecondaryColour2Change;
    private javax.swing.JButton btnSecondaryColour3Change;
    private javax.swing.JButton btnWebsite;
    private javax.swing.JButton btnWhiteColourChange;
    private javax.swing.JCheckBox chkAlwaysShowInformationScreen;
    private javax.swing.JCheckBox chkAlwaysShowShimejiChooser;
    private javax.swing.JCheckBox chkWindowModeEnabled;
    private javax.swing.JComboBox<String> cmbBackgroundImageMode;
    private javax.swing.Box.Filler glue1;
    private javax.swing.Box.Filler glue2;
    private javax.swing.Box.Filler glueBackground;
    private javax.swing.Box.Filler glueBackground2;
    private javax.swing.Box.Filler glueBlackColoura;
    private javax.swing.Box.Filler glueBlackColourb;
    private javax.swing.Box.Filler gluePrimaryColour1a;
    private javax.swing.Box.Filler gluePrimaryColour1b;
    private javax.swing.Box.Filler gluePrimaryColour2a;
    private javax.swing.Box.Filler gluePrimaryColour2b;
    private javax.swing.Box.Filler gluePrimaryColour3a;
    private javax.swing.Box.Filler gluePrimaryColour3b;
    private javax.swing.Box.Filler glueSecondaryColour1a;
    private javax.swing.Box.Filler glueSecondaryColour1b;
    private javax.swing.Box.Filler glueSecondaryColour2a;
    private javax.swing.Box.Filler glueSecondaryColour2b;
    private javax.swing.Box.Filler glueSecondaryColour3a;
    private javax.swing.Box.Filler glueSecondaryColour3b;
    private javax.swing.Box.Filler glueWhiteColoura;
    private javax.swing.Box.Filler glueWhiteColourb;
    private javax.swing.ButtonGroup grpFilter;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBackground;
    private javax.swing.JLabel lblBackgroundColour;
    private javax.swing.JLabel lblBackgroundImage;
    private javax.swing.JLabel lblBackgroundImageCaption;
    private javax.swing.JLabel lblBlackColour;
    private javax.swing.JLabel lblDevelopedBy;
    private javax.swing.JLabel lblDimensions;
    private javax.swing.JLabel lblDimensionsX;
    private javax.swing.JLabel lblFilter;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblKilkakon;
    private javax.swing.JLabel lblMenuOpacity;
    private javax.swing.JLabel lblOpacity;
    private javax.swing.JLabel lblPrimaryColour1;
    private javax.swing.JLabel lblPrimaryColour2;
    private javax.swing.JLabel lblPrimaryColour3;
    private javax.swing.JLabel lblScaling;
    private javax.swing.JLabel lblSecondaryColour1;
    private javax.swing.JLabel lblSecondaryColour2;
    private javax.swing.JLabel lblSecondaryColour3;
    private javax.swing.JLabel lblShimejiEE;
    private javax.swing.JLabel lblVersion;
    private javax.swing.JLabel lblWhiteColour;
    private javax.swing.JList lstInteractiveWindows;
    private javax.swing.JPanel pnlAbout;
    private javax.swing.JPanel pnlAboutButtons;
    private javax.swing.JPanel pnlBackgroundImage;
    private javax.swing.JPanel pnlBackgroundPreview;
    private javax.swing.JPanel pnlBackgroundPreviewContainer;
    private javax.swing.JPanel pnlBlackColourPreview;
    private javax.swing.JPanel pnlBlackColourPreviewContainer;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JPanel pnlGeneral;
    private javax.swing.JPanel pnlInteractiveButtons;
    private javax.swing.JPanel pnlInteractiveWindows;
    private javax.swing.JPanel pnlPrimaryColour1Preview;
    private javax.swing.JPanel pnlPrimaryColour1PreviewContainer;
    private javax.swing.JPanel pnlPrimaryColour2Preview;
    private javax.swing.JPanel pnlPrimaryColour2PreviewContainer;
    private javax.swing.JPanel pnlPrimaryColour3Preview;
    private javax.swing.JPanel pnlPrimaryColour3PreviewContainer;
    private javax.swing.JPanel pnlSecondaryColour1Preview;
    private javax.swing.JPanel pnlSecondaryColour1PreviewContainer;
    private javax.swing.JPanel pnlSecondaryColour2Preview;
    private javax.swing.JPanel pnlSecondaryColour2PreviewContainer;
    private javax.swing.JPanel pnlSecondaryColour3Preview;
    private javax.swing.JPanel pnlSecondaryColour3PreviewContainer;
    private javax.swing.JTabbedPane pnlTabs;
    private javax.swing.JPanel pnlTheme;
    private javax.swing.JPanel pnlThemeButtons;
    private javax.swing.JPanel pnlWhiteColourPreview;
    private javax.swing.JPanel pnlWhiteColourPreviewContainer;
    private javax.swing.JPanel pnlWindowMode;
    private javax.swing.JRadioButton radFilterBicubic;
    private javax.swing.JRadioButton radFilterHqx;
    private javax.swing.JRadioButton radFilterNearest;
    private javax.swing.Box.Filler rigid1;
    private javax.swing.Box.Filler rigid2;
    private javax.swing.Box.Filler rigid3;
    private javax.swing.Box.Filler rigid4;
    private javax.swing.JSlider sldMenuOpacity;
    private javax.swing.JSlider sldOpacity;
    private javax.swing.JSlider sldScaling;
    private javax.swing.JSpinner spnWindowHeight;
    private javax.swing.JSpinner spnWindowWidth;
    private javax.swing.JTextField txtBackgroundColour;
    private javax.swing.JTextField txtBlackColour;
    private javax.swing.JTextField txtPrimaryColour1;
    private javax.swing.JTextField txtPrimaryColour2;
    private javax.swing.JTextField txtPrimaryColour3;
    private javax.swing.JTextField txtSecondaryColour1;
    private javax.swing.JTextField txtSecondaryColour2;
    private javax.swing.JTextField txtSecondaryColour3;
    private javax.swing.JTextField txtWhiteColour;
    // End of variables declaration//GEN-END:variables
}
