// Copyright (c) 2026 STZ Robotics
// Open Source Software; you can modify and/or share it under the terms of
// the MIT license file in the root directory of this project.

package frc.robot.configuration.bindings;

import com.stzteam.mars.models.containers.Binding;
import com.stzteam.mars.operator.ControllerOI;
import com.stzteam.mars.services.nodes.Node;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.configuration.advantageScope.visuals.nodes.gamepiece.GamePieceNode.GamePieceMsg;
import frc.robot.configuration.advantageScope.visuals.nodes.trajectory.TrajectoryNode.TrajectoryMsg;
import frc.robot.configuration.constants.Constants;
import frc.robot.core.modules.superstructure.composite.Superstructure;
import frc.robot.core.modules.superstructure.modules.armmodule.ArmIOKraken.ArmMODE;
import frc.robot.core.modules.superstructure.modules.intakemodule.IntakeIOKraken.intakeMODE;
import frc.robot.core.requests.moduleRequests.ArmRequestFactory;
import frc.robot.core.requests.moduleRequests.FlyWheelRequestFactory;
import frc.robot.core.requests.moduleRequests.IntakeRequestFactory;
import frc.robot.core.requests.moduleRequests.TurretRequestFactory;

public class OperatorBindings implements Binding {

  private final ControllerOI operator;
  private final Superstructure superstructure;

  private final double DEADBAND = 0.1;

  private Node<GamePieceMsg> gamePieceViz;
  private Node<TrajectoryMsg> trajectoryNode;

  private OperatorBindings(ControllerOI operator, Superstructure superstructure) {
    this.operator = operator;
    this.superstructure = superstructure;
  }

  public static OperatorBindings create(ControllerOI operator, Superstructure ss) {
    return new OperatorBindings(operator, ss);
  }

  public OperatorBindings withNodes(Node<GamePieceMsg> gp, Node<TrajectoryMsg> tr) {
    this.gamePieceViz = gp;
    this.trajectoryNode = tr;
    return this;
  }

  @Override
  public void bind() {
    var buttons = operator.getActionButtons();
    var bumpers = operator.getBumpers();
    // var driverSystem = operator.getSystemTriggers();
    var leftStick = operator.getLeftStick();
    var rightStick = operator.getRightStick();
    var triggers = operator.getAnalogTriggers();
    var pov = operator.getDPadTriggers();

    Trigger rightStickXTrigger =
        new Trigger(() -> Math.abs(rightStick.x().getAsDouble()) > DEADBAND);
    Trigger rightStickYTrigger =
        new Trigger(() -> Math.abs(rightStick.y().getAsDouble()) > DEADBAND);
    Trigger leftStickXTrigger = new Trigger(() -> Math.abs(leftStick.x().getAsDouble()) > DEADBAND);
    Trigger leftStickYTrigger = new Trigger(() -> Math.abs(leftStick.y().getAsDouble()) > DEADBAND);

    // TODO: Terminar los bindings del operador

    // --------------------------------------------------------------- MANDO
    // ---------------------------------------------------------------

    // ----- Botones (a,b,x,y) -----

    buttons
        .bottom()
        .whileTrue(
            superstructure
                .getIntake()
                .setControl(
                    () ->
                        IntakeRequestFactory.setAngle() // Bajar el intake (a)
                            .withAngle(-125)
                            .Tolerance(Constants.INTAKE_TOLERANCE)
                            .withMode(intakeMODE.kDOWN)));
    /*
    buttons
        .top()
        .whileTrue(
            superstructure
                .getIntake()
                .setControl(
                    () ->
                        IntakeRequestFactory.setAngle() // Bubir el intake (y)
                            .withAngle(-10)
                            .Tolerance(Constants.INTAKE_TOLERANCE)
                            .withMode(intakeMODE.kUP)));*/

    buttons.right().whileTrue(superstructure.eatCommand()); // Comer fuels

    // ----- Botones (a,b,x,y) -----

    bumpers.left().whileTrue(superstructure.clearFuel());
    /*
    pov.up()
        .whileTrue(
            superstructure.ShootAngleTest(
                () -> superstructure.getAngle(), () -> superstructure.getRPM()));*/

    triggers
        .right()
        .and(bumpers.right().negate())
        .whileTrue(
            superstructure.shootOnTheMove(
                superstructure.getVirtualTarget(),
                ArmRequestFactory.interpolateTarget()
                    .withDistance(() -> superstructure.getVirtualDistance())
                    .withTolerance(Constants.ARM_TOLERANCE),
                FlyWheelRequestFactory.interpolateRPM()
                    .withDistance(() -> superstructure.getVirtualDistance())
                    .withTolerance(Constants.FLYWHEEL_TOLERANCE),
                12));
    triggers
        .right()
        .and(bumpers.right())
        .whileTrue(
            superstructure.shootOnTheMove(
                superstructure.getVirtualTarget(),
                ArmRequestFactory.interpolateTarget()
                    .withDistance(() -> superstructure.getVirtualDistance())
                    .withTolerance(Constants.ARM_TOLERANCE),
                FlyWheelRequestFactory.interpolateRPM()
                    .withDistance(() -> superstructure.getVirtualDistance())
                    .withTolerance(Constants.FLYWHEEL_TOLERANCE),
                -12));

    triggers
        .left()
        .and(bumpers.right().negate())
        .whileTrue(
            superstructure.shootOnTheMove(
                new Translation2d(0.863, 4.003),
                ArmRequestFactory.setAngle().withAngle(-25).withMode(ArmMODE.kUP),
                FlyWheelRequestFactory.setRPM().toRPM(-4000).withTolerance(50),
                12));

    triggers
        .left()
        .and(bumpers.right())
        .whileTrue(
            superstructure.shootOnTheMove(
                new Translation2d(0.863, 4.003),
                ArmRequestFactory.setAngle().withAngle(-35).withMode(ArmMODE.kUP),
                FlyWheelRequestFactory.setRPM().toRPM(-2500).withTolerance(50),
                -12));

    rightStickXTrigger
        .and(pov.right())
        .whileTrue(
            superstructure
                .getTurret()
                .setControl(() -> TurretRequestFactory.manualControl().joystick(rightStick.x())));
    rightStickYTrigger
        .and(pov.right())
        .whileTrue(
            superstructure
                .getFlywheelShooter()
                .setControl(() -> FlyWheelRequestFactory.manualShoot().getStick(rightStick.y())));

    leftStickYTrigger
        .and(pov.left())
        .whileTrue(
            superstructure
                .getArm()
                .setControl(() -> ArmRequestFactory.manualControl().joystick(leftStick.y())));

    // --------------------------------------------------------------- MANDO
    // ---------------------------------------------------------------

  }
}
