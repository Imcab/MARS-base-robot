package frc.robot.configuration.constants;

import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathConstraints;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;

public class SwerveConstants {

    public static final double kSimLoopPeriod = 0.004;

    public static final Rotation2d kBlueAlliancePerspectiveRotation = Rotation2d.kZero;
    public static final Rotation2d kRedAlliancePerspectiveRotation = Rotation2d.k180deg;

    public static final PathConstraints pathConstraints = new PathConstraints(
        4.5, 4.0, Units.degreesToRadians(540), Units.degreesToRadians(720)
    );
    
    public static PPHolonomicDriveController pathplannerPID = new PPHolonomicDriveController(
        new PIDConstants(5.0, 0.0, 0.0), 
        new PIDConstants(5.0, 0.0, 0.0)  
    );
    
    
}
